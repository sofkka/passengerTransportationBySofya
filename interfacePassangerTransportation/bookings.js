document.addEventListener("DOMContentLoaded", () => {
  const user = JSON.parse(sessionStorage.getItem("currentUser"));
  if (!user) {
    window.location.href = "index.html"; // Если нет пользователя, возвращаемся на страницу входа
    return;
  }

  // Отображаем имя пользователя рядом с иконкой
  const nameAccount = document.getElementById("name-account");
  if (nameAccount) {
    nameAccount.textContent = user.userName; // Используем textContent вместо text
  }

  fetchBookings(user.idUser);

  // Обработчик для кнопки профиля
  document.getElementById("user-profile").addEventListener("click", () => {
    window.location.href = "account.html";
  });
});

function fetchBookings(userId) {
  const url = `http://localhost:8080/bookings/user/${userId}`;
  console.log("Fetching URL:", url);

  fetch(url)
    .then((response) => {
      if (response.status === 404) {
        showNoBookingsMessage();
        return [];
      }
      if (!response.ok) {
        throw new Error(`Ошибка: ${response.status}`);
      }
      return response.json();
    })
    .then((bookings) => {
      const bookingResults = document.getElementById("booking-results");
      bookingResults.style.display = "grid";
      if (bookings.length === 0) {
        showNoBookingsMessage();
      } else {
        displayBookings(bookings);
      }
    })
    .catch((error) => {
      console.error("Ошибка:", error);
      showNoBookingsMessage();
    });
}

function showNoBookingsMessage() {
  const bookingResults = document.getElementById("booking-results");
  bookingResults.style.display = "grid";
  bookingResults.innerHTML = `
        <div class="no-bookings-message">
            <h>У вас пока нет бронирований 😔</h>
        </div>
    `;
}

// Функция для форматирования даты и времени
function formatDateTime(dateTimeString) {
  const date = new Date(dateTimeString);
  return date.toLocaleString("ru-RU", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

// Функция для вычисления времени в пути
function getTimeInRoute(departureTime, arrivalTime) {
  const timeDifference = arrivalTime - departureTime;
  const hours = Math.floor(timeDifference / (1000 * 60 * 60));
  const minutes = Math.floor((timeDifference % (1000 * 60 * 60)) / (1000 * 60));
  return `${hours} ч ${minutes} мин`;
}

function displayBookings(bookings) {
  const routesList = document.querySelector(".booking-results");
  routesList.innerHTML = ""; // Очистка списка перед добавлением новых данных

  if (!bookings || bookings.length === 0) {
    showNoBookingsMessage();
    return;
  }

  const now = new Date();
  const oneDayInMs = 24 * 60 * 60 * 1000;
  const twoHoursInMs = 2 * 60 * 60 * 1000;

  // Разделяем бронирования на те, что начинаются в ближайшие 24 часа и остальные
  const upcomingBookings = [];
  const laterBookings = [];

  bookings.forEach((booking) => {
    const departureTime = new Date(booking.route.departureDateTime);
    if (departureTime - now <= oneDayInMs && departureTime > now) {
      upcomingBookings.push(booking);
    } else {
      laterBookings.push(booking);
    }
  });

  // Сортируем обе группы по времени отправления
  upcomingBookings.sort(
    (a, b) =>
      new Date(a.route.departureDateTime) - new Date(b.route.departureDateTime)
  );
  laterBookings.sort(
    (a, b) =>
      new Date(a.route.departureDateTime) - new Date(b.route.departureDateTime)
  );

  // Объединяем: сначала ближайшие, затем остальные
  const sortedBookings = [...upcomingBookings, ...laterBookings];

  // Отображаем каждое бронирование
  sortedBookings.forEach((booking) => {
    const departureTime = new Date(booking.route.departureDateTime);
    const arrivalTime = new Date(booking.route.arrivalDateTime);

    const timeInRoute = getTimeInRoute(departureTime, arrivalTime);
    const formattedDeparture = formatDateTime(booking.route.departureDateTime);
    const formattedArrival = formatDateTime(booking.route.arrivalDateTime);
    const formattedPrice = booking.route.price.toLocaleString("ru-RU") + " ₽";
    const formattedBookingTime = formatDateTime(booking.bookingDateTime);

    let transportIcon = "";
    switch (booking.route.transportType.idTransport) {
      case "Т01":
        transportIcon = "plane.png";
        break;
      case "Т02":
        transportIcon = "bus.png";
        break;
      case "Т03":
        transportIcon = "train.png";
        break;
      default:
        transportIcon = "transport.png";
    }

    const routeContainer = document.createElement("div");
    routeContainer.className = "bookings-container";
    routeContainer.innerHTML = `
      <!-- карточка маршрута -->
      <div class="card" data-route-id="${booking.idBooking}">
        <div class="card-top">
          <div class="card-header">
            <img src="images/${transportIcon}" alt="${
      booking.route.transportType.transportName
    }" class="transport-icon">
            <h3>${booking.route.transportType.transportName}</h3>
          </div>
          <div class="card-header-price">
            <h3 class="price">${formattedPrice}</h3>
          </div>
          <div class="card-header-right">
            <span id="card-header-right"></span>
          </div>
        </div>

        <div class="points">
          <div class="departure-destination-acc">
            <h2>${booking.route.departureCity.cityName}</h2>
            <p>${formattedDeparture}</p>
          </div>
                      
                        <div class="time-in-route-arrow">
                            <span class="time-in-route">${timeInRoute}</span>
                            <svg class="arrow" width="140" height="20" viewBox="0 0 197 30" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M39.5 21.5L39.1467 23.4685L39.252 23.4874L39.3588 23.495L39.5 21.5ZM96 25.5L95.8588 27.495L96.0084 27.5056L96.1579 27.4938L96 25.5ZM146.5 21.5L146.658 23.4938L146.754 23.4862L146.849 23.4694L146.5 21.5ZM196.139 14.1458C196.772 13.2406 196.551 11.9936 195.646 11.3607L180.894 1.04722C179.988 0.414335 178.741 0.635148 178.108 1.54042C177.476 2.4457 177.696 3.69263 178.602 4.32552L191.715 13.4931L182.547 26.6063C181.914 27.5116 182.135 28.7585 183.04 29.3914C183.946 30.0243 185.193 29.8034 185.826 28.8982L196.139 14.1458ZM0.146653 16.4686L15.381 19.2029L16.0877 15.2658L0.853306 12.5315L0.146653 16.4686ZM23.9123 20.7342L39.1467 23.4685L39.8533 19.5315L24.6189 16.7971L23.9123 20.7342ZM39.3588 23.495L50.3939 24.2763L50.6764 20.2862L39.6412 19.505L39.3588 23.495ZM56.5736 24.7138L78.6439 26.2763L78.9264 22.2862L56.8561 20.7237L56.5736 24.7138ZM84.8236 26.7138L95.8588 27.495L96.1412 23.505L85.1061 22.7237L84.8236 26.7138ZM96.1579 27.4938L106.021 26.7125L105.705 22.725L95.8421 23.5062L96.1579 27.4938ZM111.545 26.275L131.271 24.7125L130.955 20.725L111.229 22.2875L111.545 26.275ZM136.795 24.275L146.658 23.4938L146.342 19.5062L136.479 20.2875L136.795 24.275ZM146.849 23.4694L156.224 21.8092L155.526 17.8705L146.151 19.5306L146.849 23.4694ZM161.474 20.8795L180.224 17.5591L179.526 13.6204L160.776 16.9408L161.474 20.8795ZM185.474 16.6294L194.849 14.9693L194.151 11.0305L184.776 12.6907L185.474 16.6294Z" fill="#44A1B1"/>
                            </svg>
                        </div>
                      
                        <div class="departure-destination-acc">
            <h2>${booking.route.arrivalCity.cityName}</h2>
            <p>${formattedArrival}</p>
          </div>

          <div class="card-body">
            <h><strong>Дата бронирования:</strong> ${formattedBookingTime}</h>
            <div class="booking-details">
              <h><strong>Багаж:</strong> ${
                booking.withBaggage ? "Да" : "Нет"
              }</h>
              <br>
              <h><strong>Дети:</strong> ${
                booking.withChild > 0 ? booking.withChild : "Нет"
              }</h>
              <br>
              <h><strong>Животные:</strong> ${
                booking.withPet > 0 ? booking.withPet : "Нет"
              }</h>
            </div>
          </div>
        </div>

        <button class="button button-cancel" data-booking-id="${
          booking.idBooking
        }">Отменить бронь</button>
      </div>
    `;

    // Обработка кнопки "Отменить бронь"
    const cancelButton = routeContainer.querySelector(".button-cancel");
    if (cancelButton) {
      const cardHeaderRight =
        routeContainer.querySelector("#card-header-right");

      // Если рейс уже прошёл
      if (departureTime < now) {
        cancelButton.classList.add("disabled-button");
        cancelButton.disabled = true;
        if (cardHeaderRight) {
          cardHeaderRight.textContent =
            "Отмена бронирования невозможна. Рейс уже прошёл.";
        }
        cancelButton.addEventListener("click", () => {
          alert("Отмена бронирования невозможна. Рейс уже прошёл.");
        });
      }
      // Если до отправления менее 2 часов
      else if (departureTime - now <= twoHoursInMs) {
        cancelButton.classList.add("disabled-button");
        cancelButton.disabled = true;
        if (cardHeaderRight) {
          cardHeaderRight.textContent =
            "Отмена бронирования невозможна. До отправления осталось менее 2 часов.";
        }
        cancelButton.addEventListener("click", () => {
          alert(
            "Отмена бронирования невозможна. До отправления осталось менее 2 часов."
          );
        });
      }
      // Если можно отменить
      else {
        cancelButton.addEventListener("click", function () {
          const bookingId = this.getAttribute("data-booking-id");
          deleteBooking(bookingId);
        });
      }
    }

    routesList.appendChild(routeContainer); // Добавление карточки в DOM
  });

  document.getElementById("booking-results").style.display = "grid";
}

function deleteBooking(bookingId) {
  if (!bookingId) {
    console.error("Booking ID is undefined or invalid.");
    showMessage("Ошибка: ID бронирования не определен.", "error");
    return;
  }

  const isConfirmed = confirm(
    "Вы уверены, что хотите отменить это бронирование?"
  );
  if (!isConfirmed) return;

  fetch(`http://localhost:8080/bookings/${bookingId}`, {
    method: "DELETE",
  })
    .then((response) => {
      if (!response.ok) {
        return response.text().then((text) => {
          throw new Error(text || "Ошибка при удалении бронирования.");
        });
      }
      return response.text();
    })
    .then((message) => {
      console.log("Бронирование отменено:", message);
      const user = JSON.parse(sessionStorage.getItem("currentUser"));
      fetchBookings(user.idUser);
    })
    .catch((error) => {
      console.error("Ошибка:", error);
      alert("Ошибка: " + error.message);
    });
}

function showMessage(message, type) {
  const resultContainer = document.getElementById("booking-results");
  resultContainer.innerHTML = `<div class="message ${type}">${message}</div>`;
}
