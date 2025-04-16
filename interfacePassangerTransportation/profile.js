// Ждём полной загрузки DOM перед выполнением скрипта
document.addEventListener("DOMContentLoaded", () => {
  // Пытаемся получить текущего пользователя из sessionStorage
  const user = JSON.parse(sessionStorage.getItem("currentUser"));

  // Если пользователь не найден, перенаправляем на страницу входа
  if (!user) {
    window.location.href = "index.html";
    return;
  }

  // Отображаем информацию профиля
  displayProfile(user);

  // Назначаем обработчик для кнопки "Редактировать профиль"
  document
    .getElementById("edit-profile")
    .addEventListener("click", () => editProfile(user));

  // Обработчик кнопки возврата к бронированиям
  document.getElementById("back-to-bookings").addEventListener("click", () => {
    window.location.href = "usersBookings.html";
  });

  // Обработчик кнопки выхода из аккаунта
  document.getElementById("logout-button").addEventListener("click", () => {
    const confirmLogout = confirm("Вы действительно хотите выйти?");
    if (confirmLogout) {
      sessionStorage.removeItem("currentUser"); // Удаляем пользователя из сессии
      window.location.href = "index.html"; // Переход на страницу входа
    }
  });
});

// Отображение полей профиля в режиме просмотра (только для чтения)
function displayProfile(user) {
  const profileInfo = document.getElementById("profile-info");
  profileInfo.innerHTML = `
        <div class="input-group">
            <label>Логин:</label>
            <input type="text" value="${user.login}" disabled>
        </div>
        <div class="input-group">
            <label>Телефон:</label>
            <input type="text" value="${user.phoneNumber}" disabled>
        </div>
        <div class="input-group">
            <label>Фамилия:</label>
            <input type="text" value="${user.userSurname}" disabled>
        </div>
        <div class="input-group">
            <label>Имя:</label>
            <input type="text" value="${user.userName}" disabled>
        </div>
        <div class="input-group">
            <label>Отчество:</label>
            <input type="text" value="${user.userPatronymic || "Не указано"}" disabled>
        </div>
    `;
}

// Отображение полей профиля в режиме редактирования
function editProfile(user) {
  const profileInfo = document.getElementById("profile-info");
  profileInfo.innerHTML = `
        <div class="input-group">
            <label>Логин:</label>
            <input type="text" id="edit-login" value="${user.login}" disabled>
        </div>
        <div class="input-group">
            <label>Телефон:</label>
            <input type="tel" id="edit-phone" value="${user.phoneNumber}">
        </div>
        <div class="input-group">
            <label>Фамилия:</label>
            <input type="text" id="edit-surname" value="${user.userSurname}">
        </div>
        <div class="input-group">
            <label>Имя:</label>
            <input type="text" id="edit-name" value="${user.userName}">
        </div>
        <div class="input-group">
            <label>Отчество:</label>
            <input type="text" id="edit-patronymic" value="${user.userPatronymic || ""}">
        </div>
        <div class="input-group">
            <button class="button" id="save-profile">Сохранить изменения</button>
            <button class="button" id="cancel-profile">Отменить изменения</button>
        </div>
    `;

  // Назначение обработчиков для кнопок "Сохранить" и "Отменить"
  document
    .getElementById("save-profile")
    .addEventListener("click", () => saveProfile(user));

  document
    .getElementById("cancel-profile")
    .addEventListener("click", () => displayProfile(user));
}

// Сохранение обновлённых данных пользователя
function saveProfile(user) {
  // Формируем объект с обновлёнными данными
  const updatedUser = {
    idUser: user.idUser,
    login: user.login,
    phoneNumber: document.getElementById("edit-phone").value.trim(),
    userSurname: document.getElementById("edit-surname").value.trim(),
    userName: document.getElementById("edit-name").value.trim(),
    userPatronymic: document.getElementById("edit-patronymic").value.trim() || null,
  };

  // Проверка обязательных полей
  if (
    !updatedUser.phoneNumber ||
    !updatedUser.userSurname ||
    !updatedUser.userName
  ) {
    alert("Телефон, фамилия и имя обязательны!");
    return;
  }

  // Валидация формата телефона (пример: +79991234567)
  if (!/^\+7\d{10}$/.test(updatedUser.phoneNumber.replace(/[^+\d]/g, ""))) {
    alert("Номер телефона должен быть в формате +79991234567!");
    return;
  }

  // Валидация имени (только кириллица и дефисы, минимум 2 буквы)
  if (
    !/^[а-яА-ЯёЁ-]+$/u.test(updatedUser.userName) ||
    updatedUser.userName.replace(/-/g, "").length < 2
  ) {
    alert("Имя должно содержать минимум 2 буквы (только кириллица и дефисы)!");
    return;
  }

  // Валидация фамилии (аналогично имени)
  if (
    !/^[а-яА-ЯёЁ-]+$/u.test(updatedUser.userSurname) ||
    updatedUser.userSurname.replace(/-/g, "").length < 2
  ) {
    alert("Фамилия должна содержать минимум 2 буквы (только кириллица и дефисы)!");
    return;
  }

  // Валидация отчества, если указано (только кириллица и дефисы)
  if (
    updatedUser.userPatronymic &&
    !/^[а-яА-ЯёЁ-]+$/u.test(updatedUser.userPatronymic)
  ) {
    alert("Отчество должно содержать только кириллицу и дефисы!");
    return;
  }

  // Отправляем PUT-запрос на сервер для обновления данных пользователя
  fetch(`http://localhost:8080/users/${user.idUser}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(updatedUser),
  })
    .then((response) => {
      if (!response.ok) {
        return response.text().then((text) => {
          throw new Error(text || "Ошибка при обновлении профиля");
        });
      }
      return response.json();
    })
    .then((updated) => {
      // Обновляем данные в sessionStorage и возвращаемся в режим просмотра
      sessionStorage.setItem("currentUser", JSON.stringify(updated));
      displayProfile(updated);
      alert("Профиль успешно обновлён!");
    })
    .catch((error) => {
      console.error("Ошибка:", error);
      alert("Ошибка при сохранении профиля: " + error.message);
    });
}
