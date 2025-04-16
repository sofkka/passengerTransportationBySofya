// При загрузке страницы
document.addEventListener("DOMContentLoaded", () => {
  // Проверяем, есть ли текущий пользователь
  const user = JSON.parse(sessionStorage.getItem("currentUser"));
  // Если пользователь есть, обновляем шапку сайта
  if (user) {
    updateHeader(user);
  }

  // Загружаем типы транспорта и города для формы поиска
  Promise.all([
    fetch("http://localhost:8080/transport-types"), // Запрос на типы транспорта
    fetch("http://localhost:8080/cities"), // Запрос на города
  ])
    .then(([transportRes, citiesRes]) =>
      Promise.all([transportRes.json(), citiesRes.json()]) // Преобразуем ответы в JSON
    )
    .then(([transports, cities]) => {
      // Заполняем выпадающие списки данными
      displayTransports(transports);
      displayDeparturePoint(cities);
      displayArrivalPoint(cities);
    })
    .catch((error) => {
      // Если что-то пошло не так, показываем ошибку
      console.error("Ошибка загрузки данных:", error);
      alert("Сервер не отвечает. Попробуйте позже.");
    });

  // Загружаем маршруты с пагинацией
  loadRoutes();

  // Добавляем обработчики для кнопки поиска, чекбокса и пагинации
  document
    .getElementById("button-search")
    .addEventListener("click", () => {
      currentPage = 0; // Сбрасываем на первую страницу при новом поиске
      clickButtonSearch();
    });
  document
    .getElementById("checkbox-departure-date-interval")
    .addEventListener("click", changeDepartureDateInterval);
  document.getElementById("prev-page").addEventListener("click", () => {
    if (currentPage > 0) {
      currentPage--; // Переходим на предыдущую страницу
      loadRoutes();
      window.scrollTo({ top: 0, behavior: "auto" }); // Прокручиваем наверх
    }
  });
  document.getElementById("next-page").addEventListener("click", () => {
    currentPage++; // Переходим на следующую страницу
    loadRoutes();
    window.scrollTo({ top: 0, behavior: "auto" }); // Прокручиваем наверх
  });

  // Настраиваем закрытие модальных окон
  const modals = document.querySelectorAll(".modal");
  modals.forEach((modal) => {
    const closeButton = modal.querySelector(".close");
    if (closeButton) {
      closeButton.onclick = () => (modal.style.display = "none");
    }
    window.addEventListener("click", (event) => {
      if (event.target === modal) modal.style.display = "none";
    });
  });
});

// Глобальные переменные для пагинации и поиска
let currentPage = 0; // Текущая страница
const pageSize = 5; // Количество маршрутов на странице
let lastSearchParams = null; // Сохраняем параметры последнего поиска

// Обновляем шапку сайта
function updateHeader(user) {
  const nameAccount = document.getElementById("name-account");
  const hrefAccount = document.getElementById("href-account");
  if (user) {
    nameAccount.textContent = user.userName;
    hrefAccount.href = "usersBookings.html";
  } else {
    nameAccount.textContent = "Войти в аккаунт";
    hrefAccount.href = "login.html";
  }
}

// Заполняем выпадающий список с типами транспорта
function displayTransports(transports) {
  if (!transports || !Array.isArray(transports)) {
    console.error("Ошибка: данные транспортов не переданы");
    return;
  }

  const transportList = document.getElementById("transport-type");
  transportList.innerHTML = "";

  const defaultOption = document.createElement("option");
  defaultOption.value = "";
  defaultOption.textContent = "Выберите тип транспорта";
  defaultOption.selected = true;
  defaultOption.disabled = true;
  transportList.appendChild(defaultOption);

  const mixOption = document.createElement("option");
  mixOption.value = "Микс";
  mixOption.textContent = "Микс";
  transportList.appendChild(mixOption);

  transports.forEach((transport) => {
    const option = document.createElement("option");
    option.value = transport.idTransport;
    option.textContent = transport.transportName;
    transportList.appendChild(option);
  });
}

// Заполняем выпадающий список с городами отправления
function displayDeparturePoint(points) {
  const departurePointList = document.getElementById("departure");
  departurePointList.innerHTML = "";

  const defaultOption = document.createElement("option");
  defaultOption.value = "";
  defaultOption.textContent = "Выберите пункт отправления";
  defaultOption.selected = true;
  defaultOption.disabled = true;
  departurePointList.appendChild(defaultOption);

  points.forEach((point) => {
    const option = document.createElement("option");
    option.value = point.idCity;
    option.textContent = point.cityName;
    departurePointList.appendChild(option);
  });
}

// Заполняем выпадающий список с городами прибытия
function displayArrivalPoint(points) {
  const arrivalPointList = document.getElementById("arrival");
  arrivalPointList.innerHTML = "";

  const defaultOption = document.createElement("option");
  defaultOption.value = "";
  defaultOption.textContent = "Выберите пункт прибытия";
  defaultOption.selected = true;
  defaultOption.disabled = true;
  arrivalPointList.appendChild(defaultOption);

  points.forEach((point) => {
    const option = document.createElement("option");
    option.value = point.idCity;
    option.textContent = point.cityName;
    arrivalPointList.appendChild(option);
  });
}

// Показываем или скрываем поля для выбора диапазона дат
function changeDepartureDateInterval() {
  if (document.getElementById("checkbox-departure-date-interval").checked) {
    document.getElementById("departure-date").style.display = "none";
    document.getElementById("departure-date").value = null;
    document.getElementById("departure-date-interval").style.display = "grid";
  } else {
    document.getElementById("departure-date").style.display = "block";
    document.getElementById("departure-date-interval").style.display = "none";
    document.getElementById("departure-date-start").value = null;
    document.getElementById("departure-date-end").value = null;
  }
}

// Обрабатываем нажатие на кнопку поиска
function clickButtonSearch() {
  const departure = document.getElementById("departure");
  const arrival = document.getElementById("arrival");
  const transportType = document.getElementById("transport-type");
  const departureDate = document.getElementById("departure-date");
  const departureDateStart = document.getElementById("departure-date-start");
  const departureDateEnd = document.getElementById("departure-date-end");
  const checkboxInterval = document.getElementById("checkbox-departure-date-interval");

  // Проверяем, что все обязательные поля заполнены
  if (!departure.value || !arrival.value || !transportType.value) {
    window.alert("Пожалуйста, заполните поля: города и тип транспорта!");
    return;
  }

  // Проверяем, что города разные
  if (departure.value === arrival.value) {
    window.alert("Города отправления и прибытия не могут совпадать!");
    return;
  }

  // Проверяем даты
  if (!checkboxInterval.checked) {
    // Если интервал не выбран, нужна одиночная дата
    if (!departureDate.value) {
      window.alert("Пожалуйста, укажите дату отправления!");
      return;
    }
  } else {
    // Если выбран интервал, нужны обе даты
    if (!departureDateStart.value || !departureDateEnd.value) {
      window.alert("Пожалуйста, укажите обе даты диапазона!");
      return;
    }
    // Если выбран интервал, нужны обе даты
    if (departureDateStart.value > departureDateEnd.value) {
      window.alert("Вторая дата интервала не может быть раньше первой!");
      return;
    }
  }

  // Формируем параметры поиска
  const params = {
    departureCityId: departure.value,
    arrivalCityId: arrival.value,
  };

  // Добавляем transportId только если не "Микс"
  if (transportType.value !== "Микс") {
    params.transportId = transportType.value;
  }

  // Добавляем даты
  if (checkboxInterval.checked) {
    params.dateOne = departureDateStart.value;
    params.dateTwo = departureDateEnd.value;
  } else {
    params.startDate = departureDate.value;
  }

  // Сохраняем параметры и загружаем маршруты
  lastSearchParams = params;
  loadRoutes();
}

// Загружаем маршруты с пагинацией
function loadRoutes() {
  let url = `http://localhost:8080/routes/paged?page=${currentPage}&size=${pageSize}`;
  // Если есть параметры для поиска формируем другую ссылку
  if (lastSearchParams) {
    const queryParams = new URLSearchParams();
    for (const [key, value] of Object.entries(lastSearchParams)) {
      if (value !== null && value !== undefined) {
        queryParams.append(key, value);
      }
    }
    url = `http://localhost:8080/routes/filteredRoutes?${queryParams.toString()}`;
  }

  fetch(url)
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Ошибка сервера: ${response.status}`);
      }
      return response.json();
    })
    .then((data) => {
      console.log("Полученные данные:", data);
      // Показываем маршруты и обновляем пагинацию
      displayRoutes(data.content || data); // Обрабатываем Page или List
      updatePagination(data.content ? data : {}, !!lastSearchParams); // Передаём флаг поиска
    })
    .catch((error) => {
      console.error("Ошибка загрузки маршрутов:", error);
      document.querySelector(".routes-list").innerHTML = `
        <div class="no-routes-message">
            <p>Ошибка загрузки маршрутов 😔</p>
            <p>Попробуйте позже.</p>
        </div>`;
      // Сбрасываем пагинацию
      updatePagination({}, !!lastSearchParams);
    });
}

// Обновляем пагинацию
function updatePagination(data, isSearch) {
  const pageInfo = document.getElementById("page-info");
  const prevButton = document.getElementById("prev-page");
  const nextButton = document.getElementById("next-page");
  const pagination = document.querySelector(".pagination");

  if (isSearch || !data || typeof data.number === "undefined") {
    // Скрываем пагинацию после поиска или при отсутствии данных
    if (pagination) pagination.style.display = "none";
    pageInfo.textContent = "";
    prevButton.disabled = true;
    nextButton.disabled = true;
    return;
  }

  // Показываем пагинацию для /paged
  if (pagination) pagination.style.display = "flex"; // или "block" в зависимости от CSS
  pageInfo.textContent = `Страница ${data.number + 1} из ${data.totalPages}`;
  prevButton.disabled = data.first;
  nextButton.disabled = data.last;
}

// Показываем маршруты на странице
function displayRoutes(routes) {
  const routesList = document.querySelector(".routes-list");
  routesList.innerHTML = "<h3>Список рейсов</h3>"; // Заголовок списка

  // Если маршрутов нет, показываем сообщение
  if (!routes || routes.length === 0) {
    routesList.innerHTML += `
            <div class="no-routes-message">
                <p>К сожалению, маршрутов не найдено 😔</p>
                <p>Попробуйте изменить параметры поиска.</p>
            </div>`;
    return;
  }

  const now = new Date();
  const oneDayInMs = 24 * 60 * 60 * 1000; // Один день
  const twoHoursInMs = 2 * 60 * 60 * 1000; // Два часа

  // Создаем карточку для каждого маршрута
  routes.forEach((route) => {
    const departureTime = new Date(route.departureDateTime);
    const arrivalTime = new Date(route.arrivalDateTime);

    // Пропускаем маршруты, которые были больше суток назад
    if (departureTime < now - oneDayInMs) return;

    // Считаем время в пути
    const timeInRoute = getTimeInRoute(departureTime, arrivalTime);
    // Форматируем даты
    const formattedDeparture = formatDateTime(route.departureDateTime);
    const formattedArrival = formatDateTime(route.arrivalDateTime);
    // Форматируем цену
    const formattedPrice = route.price.toLocaleString("ru-RU") + " ₽";

    // Выбираем иконку для транспорта
    let transportIcon = "";
    switch (route.transportType.idTransport) {
      case "Т01":
        transportIcon = "plane.png"; // Самолет
        break;
      case "Т02":
        transportIcon = "bus.png"; // Автобус
        break;
      case "Т03":
        transportIcon = "train.png"; // Поезд
        break;
      default:
        transportIcon = "transport.png"; // На всякий случай
    }

    // Создаем контейнер для карточки маршрута
    const routeElement = document.createElement("div");
    routeElement.className = "route-container";
    routeElement.innerHTML = `
            <div class="card" data-route-id="${route.idRoute}">
                <div class="card-top">
                    <div class="card-header">
                        <img src="images/${transportIcon}" alt="${
      route.transportType.transportName
    }" class="transport-icon">
                        <h3>${route.transportType.transportName}</h3>
                    </div>
                    <div class="card-header-price">
                        <h3 class="price">${formattedPrice}</h3>
                    </div>
                    <div class="card-header-right">
                        <span id="card-header-right"></span>
                    </div>
                </div>
                <div class="points">
                    <div class="departure-destination">
                        <h2>${route.departureCity.cityName}</h2>
                        <p>${formattedDeparture}</p>
                    </div>
                    <div class="time-in-route-arrow">
                        <span class="time-in-route">${timeInRoute}</span>
                        <svg class="arrow" width="197" height="30" viewBox="0 0 197 30" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M39.5 21.5L39.1467 23.4685L39.252 23.4874L39.3588 23.495L39.5 21.5ZM96 25.5L95.8588 27.495L96.0084 27.5056L96.1579 27.4938L96 25.5ZM146.5 21.5L146.658 23.4938L146.754 23.4862L146.849 23.4694L146.5 21.5ZM196.139 14.1458C196.772 13.2406 196.551 11.9936 195.646 11.3607L180.894 1.04722C179.988 0.414335 178.741 0.635148 178.108 1.54042C177.476 2.4457 177.696 3.69263 178.602 4.32552L191.715 13.4931L182.547 26.6063C181.914 27.5116 182.135 28.7585 183.04 29.3914C183.946 30.0243 185.193 29.8034 185.826 28.8982L196.139 14.1458ZM0.146653 16.4686L15.381 19.2029L16.0877 15.2658L0.853306 12.5315L0.146653 16.4686ZM23.9123 20.7342L39.1467 23.4685L39.8533 19.5315L24.6189 16.7971L23.9123 20.7342ZM39.3588 23.495L50.3939 24.2763L50.6764 20.2862L39.6412 19.505L39.3588 23.495ZM56.5736 24.7138L78.6439 26.2763L78.9264 22.2862L56.8561 20.7237L56.5736 24.7138ZM84.8236 26.7138L95.8588 27.495L96.1412 23.505L85.1061 22.7237L84.8236 26.7138ZM96.1579 27.4938L106.021 26.7125L105.705 22.725L95.8421 23.5062L96.1579 27.4938ZM111.545 26.275L131.271 24.7125L130.955 20.725L111.229 22.2875L111.545 26.275ZM136.795 24.275L146.658 23.4938L146.342 19.5062L136.479 20.2875L136.795 24.275ZM146.849 23.4694L156.224 21.8092L155.526 17.8705L146.151 19.5306L146.849 23.4694ZM161.474 20.8795L180.224 17.5591L179.526 13.6204L160.776 16.9408L161.474 20.8795ZM185.474 16.6294L194.849 14.9693L194.151 11.0305L184.776 12.6907L185.474 16.6294Z" fill="#44A1B1"/>
                        </svg>
                    </div>
                    <div class="departure-destination">
                        <h2>${route.arrivalCity.cityName}</h2>
                        <p>${formattedArrival}</p>
                    </div>
                </div>
                <div class="card-body">
                    ${
                      departureTime > now
                        ? `
                        <div class="seats-info">
                            <strong>Доступно мест:</strong> 
                            <span class="available-seats ${
                              route.availableSeats <= 20 ? "low-seats" : ""
                            }">
                                ${route.availableSeats}
                            </span>
                        </div>
                    `
                        : ""
                    }
                    <button class="button button-book">Забронировать</button>
                </div>
            </div>
        `;

    // Настраиваем кнопку "Забронировать"
    const bookButton = routeElement.querySelector(".button-book");
    if (bookButton) {
      // Если рейс прошел, отключаем кнопку
      if (departureTime < now) {
        bookButton.disabled = true;
        bookButton.classList.add("disabled-button");
        routeElement.querySelector("#card-header-right").textContent =
          "Рейс уже завершён";
      }
      // Если до рейса меньше 2 часов, тоже отключаем
      else if (departureTime - now < twoHoursInMs) {
        bookButton.disabled = true;
        bookButton.classList.add("disabled-button");
        routeElement.querySelector("#card-header-right").textContent =
          "До отправления менее 2 часов";
      }
      // Если все ок, добавляем обработчик
      else {
        bookButton.addEventListener("click", () =>
          handleBooking(route.idRoute)
        );
      }
    }

    // Если мест мало, добавляем предупреждение
    if (route.availableSeats <= 40 && departureTime > now) {
      const availableSeatsElement =
        routeElement.querySelector(".available-seats");
      if (availableSeatsElement) {
        availableSeatsElement.classList.add("low-seats");
      }

      const warningMessage = document.createElement("p");
      warningMessage.className = "warning-message";

      // Если мест нет, отключаем кнопку
      if (route.availableSeats === 0) {
        warningMessage.textContent = "Места на рейс закончились!";
        bookButton.classList.add("disabled-button");
        bookButton.disabled = true;
        routeElement.querySelector("#card-header-right").textContent =
          "Забронировать место на рейс нельзя, так как они закончились!";
      } else {
        warningMessage.textContent = "Скоро места закончатся!";
      }

      const cardBody = routeElement.querySelector(".card-body");
      if (cardBody) {
        cardBody.appendChild(warningMessage);
      }
    }

    // Добавляем карточку в список
    routesList.appendChild(routeElement);
  });
}

// Обрабатываем бронирование
function handleBooking(idRoute) {
  // Проверяем, есть ли пользователь
  const user = JSON.parse(sessionStorage.getItem("currentUser"));
  if (!user) {
    alert("Для бронирования необходимо войти в аккаунт!");
    window.location.href = "login.html";
    return;
  }

  // Открываем модальное окно для бронирования
  const bookingModal = document.getElementById("booking-modal");
  bookingModal.dataset.routeId = idRoute;
  bookingModal.innerHTML = `
    <div class="modal-content">
        <span class="close">×</span>
        <h2>Детали бронирования</h2>
        <div class="input-group">
            <label id="label-and-checkbox-baggage">
                <input type="checkbox" id="with-baggage">
                <span>Багаж</span>
            </label>
        </div>
        <div class="input-group">
            <label for="with-child">Количество детей</label>
            <input type="number" id="with-child" min="0" max="3" value="0">
        </div>
        <div class="input-group">
            <label for="with-pet">Количество животных</label>
            <input type="number" id="with-pet" min="0" max="3" value="0">
        </div>
        <button class="button" id="confirm-booking">Подтвердить</button>
    </div>
`;

  // Показываем модальное окно
  bookingModal.style.display = "block";

  // Настраиваем закрытие окна
  bookingModal.querySelector(".close").onclick = () =>
    (bookingModal.style.display = "none");
  window.addEventListener("click", (event) => {
    if (event.target === bookingModal) bookingModal.style.display = "none";
  });

  // Обрабатываем подтверждение брони
  document.getElementById("confirm-booking").addEventListener("click", () => {
    const withBaggage = document.getElementById("with-baggage").checked;
    const withChild = Number(document.getElementById("with-child").value);
    const withPet = Number(document.getElementById("with-pet").value);

    // Проверяем, что данные корректны
    if (withChild < 0 || withChild > 3) {
      alert("Количество детей должно быть от 0 до 3!");
      return;
    }
    if (withPet < 0 || withPet > 3) {
      alert("Количество животных должно быть от 0 до 3!");
      return;
    }

    // Форматируем текущую дату и время для брони
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, "0");
    const day = String(now.getDate()).padStart(2, "0");
    const hours = String(now.getHours()).padStart(2, "0");
    const minutes = String(now.getMinutes()).padStart(2, "0");
    const seconds = String(now.getSeconds()).padStart(2, "0");
    const bookingDateTime = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;

    // Формируем параметры запроса
    const params = new URLSearchParams({
      routeId: idRoute,
      userId: user.idUser,
      bookingDateTime,
      withBaggage,
      withChild,
      withPet,
    });
    const url = `http://localhost:8080/bookings/createNewBooking?${params.toString()}`;

    // Отправляем запрос на создание брони
    fetch(url, {
      method: "POST",
      headers: { Accept: "*/*" },
    })
      .then((response) => {
        if (!response.ok) {
          return response.text().then((text) => {
            throw new Error(text || "Ошибка сервера");
          });
        }
        return response.json();
      })
      .then((data) => {
        bookingModal.style.display = "none"; // Закрываем окно
        alert("Бронирование успешно создано!");
      })
      .catch((error) => {
        console.error("Ошибка при создании бронирования:", error);
        alert("Произошла ошибка при бронировании: " + error.message);
      });
  });
}

// Форматируем дату и время для отображения
function formatDateTime(dateTimeString) {
  const options = {
    day: "numeric",
    month: "numeric",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  };
  return new Date(dateTimeString).toLocaleString("ru-RU", options);
}

// Считаем время в пути
function getTimeInRoute(departureTime, arrivalTime) {
  const diffMs = arrivalTime - departureTime;
  const hours = Math.floor(diffMs / (1000 * 60 * 60));
  const minutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
  return `${hours} ч ${minutes} мин`;
}
