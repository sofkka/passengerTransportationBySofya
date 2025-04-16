// Обработка нажатия кнопки "Зарегистрироваться"
document.getElementById("button-register").addEventListener("click", () => {
    // Получаем значения из полей формы
    const login = document.getElementById("reg-login").value.trim();
    const password = document.getElementById("reg-password").value.trim();
    const passwordConfirm = document.getElementById("reg-password-confirm").value.trim();
    const name = document.getElementById("reg-name").value.trim();
    const surname = document.getElementById("reg-surname").value.trim();
    const patronymic = document.getElementById("reg-patronymic").value.trim();
    let phone = document.getElementById("reg-phone").value.trim();
  
    // Проверка обязательных полей
    if (!login || !password || !passwordConfirm || !name || !surname || !phone) {
      alert("Заполните все обязательные поля!");
      return;
    }
  
    // Проверка формата логина: латинские буквы, цифры, -, _, !, ?, минимум 3 символа
    if (!/^[a-zA-Z0-9-_!?]{3,}$/.test(login)) {
      alert("Логин должен содержать минимум 3 символа и может включать только латинские буквы, цифры и символы: -, _, !, ?");
      return;
    }
  
    // Проверка формата пароля (буквы и цифры, минимум 8 символов)
    if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/.test(password)) {
      alert("Пароль должен содержать минимум 8 символов, включая и латинские буквы и цифры!");
      return;
    }
  
    // Проверка совпадения пароля и подтверждения
    if (password !== passwordConfirm) {
      alert("Пароли не совпадают!");
      return;
    }
  
    // Проверка имени и фамилии (только буквы, минимум 2 символа)
    if (!/^[а-яА-Яa-zA-Z]{2,}$/.test(name) || !/^[а-яА-Яa-zA-Z]{2,}$/.test(surname)) {
      alert("Имя и фамилия должны содержать только буквы! (минимум 2)");
      return;
    }
  
    // Проверка отчества (если указано — только буквы)
    if (patronymic && !/^[а-яА-Яa-zA-Z]+$/.test(patronymic)) {
      alert("Отчество должно содержать только буквы!");
      return;
    }
  
    // Очистка номера телефона (оставляем только + и цифры)
    phone = phone.replace(/[^+\d]/g, "");
    if (!/^\+7\d{10}$/.test(phone)) {
      alert("Номер телефона должен быть в формате +7(999)-999-99-99!");
      return;
    }
  
    // Формируем объект с данными для отправки
    const userData = {
      login: login,
      password: password,
      phoneNumber: phone,
      userName: name,
      userSurname: surname,
      userPatronymic: patronymic || "", // если отчество не указано — пустая строка
    };
  
    // Отправка POST-запроса на сервер
    fetch("http://localhost:8080/users/createNewUser", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: new URLSearchParams(userData).toString(),
    })
      .then((response) => {
        if (!response.ok) {
          return response.text().then((text) => {
            throw new Error(text || "Ошибка регистрации пользователя");
          });
        }
        return response.json();
      })
      .then((user) => {
        // Успешная регистрация
        alert("Регистрация прошла успешно! Теперь вы можете войти в аккаунт.");
        window.location.href = "login.html";
      })
      .catch((error) => {
        // Вывод ошибки при регистрации
        console.error("Ошибка при регистрации:", error);
        alert("Ошибка при регистрации: " + error.message);
      });
  });
  
  // Переключение видимости пароля
  function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const icon = input.nextElementSibling;
    if (input.type === "password") {
      input.type = "text";
      icon.textContent = "🙈"; // показать пароль
    } else {
      input.type = "password";
      icon.textContent = "👁️"; // скрыть пароль
    }
  }
  
  // Форматирование номера телефона при вводе
  document.getElementById("reg-phone").addEventListener("input", (e) => {
    let value = e.target.value.replace(/\D/g, ""); // убираем нецифровые символы
    if (value.length > 11) value = value.slice(0, 11); // максимум 11 цифр
    if (value.startsWith("8")) value = "7" + value.slice(1); // заменяем 8 на 7
  
    let formatted = "";
    if (value.length > 0) formatted += "+7";
    if (value.length > 1) formatted += "(" + value.slice(1, 4);
    if (value.length >= 4) formatted += ")";
    if (value.length >= 4) formatted += "-" + value.slice(4, 7);
    if (value.length >= 7) formatted += "-" + value.slice(7, 9);
    if (value.length >= 9) formatted += "-" + value.slice(9, 11);
  
    // Устанавливаем новое форматированное значение
    if (e.target.value !== formatted) {
      e.target.value = formatted;
    }
  });
  