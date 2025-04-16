// Обработка клика по кнопке "Забыли пароль"
document.getElementById("button_forgot_pass").addEventListener("click", () => {
  alert(
    "Ссылка для изменения вашего пароля будет направлена вам на номер телефона"
  );
});

// Обработка клика по кнопке "Войти в аккаунт"
document
  .getElementById("button-enter-acc")
  .addEventListener("click", clickButtonEnter);

// Функция обработки входа в аккаунт
function clickButtonEnter(event) {
  event.preventDefault(); // Отменяем стандартное поведение формы

  // Получаем элементы логина и пароля
  const loginInput = document.getElementById("input-login");
  const passwordInput = document.getElementById("input-password");

  // Проверка наличия полей
  if (!loginInput || !passwordInput) {
    alert("Ошибка: форма входа не найдена на странице!");
    console.error(
      "Один из элементов (input-login или input-password) не найден."
    );
    return;
  }

  // Получаем значения из полей и убираем пробелы
  const login = loginInput.value.trim();
  const password = passwordInput.value.trim();

  // Проверка на пустые поля
  if (!login || !password) {
    alert("Заполните логин и пароль!");
    return;
  }

  // Формируем URL с параметрами
  const params = new URLSearchParams({ login, password }).toString();
  const url = `http://localhost:8080/users/login?${params}`;

  // Отправка GET-запроса
  fetch(url, {
    method: "GET",
    headers: { "Content-Type": "application/json" },
  })
    .then((response) => {
      if (!response.ok) {
        return response.text().then((text) => {
          throw new Error(text || "Неверный логин или пароль");
        });
      }
      return response.json();
    })
    .then((user) => {
      // Успешный вход: сохраняем пользователя и переходим на следующую страницу
      sessionStorage.setItem("currentUser", JSON.stringify(user));
      alert("Вход выполнен успешно!");
      window.location.href = "index.html";
    })
    .catch((error) => {
      // Ошибка входа
      console.error("Ошибка входа:", error);
      alert("Ошибка: " + error.message);
    });
}

// Показывает сообщение в контейнер (если нужно визуально на странице)
function showMessage(message, type) {
  const resultContainer = document.getElementById("booking-results");
  if (resultContainer) {
    resultContainer.innerHTML = `<div class="message ${type}">${message}</div>`;
  }
}

// Переключение видимости пароля
function togglePassword(inputId) {
  const input = document.getElementById(inputId);
  const icon = input.nextElementSibling;
  if (input.type === "password") {
    input.type = "text";
    icon.textContent = "🙈"; // Скрытие отключено
  } else {
    input.type = "password";
    icon.textContent = "👁️"; // Пароль скрыт
  }
}
