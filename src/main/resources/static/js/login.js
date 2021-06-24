const form = document.getElementById('loginForm');
const login = document.getElementById('login');
const password = document.getElementById('password');

const validationErrorMessage = document.getElementById('validationErrorMessage');

const loginRegExp = /^(?!.*\.\.)(?!.*\.$)[^\W][\w.]{4,20}$/;
const passwordRegExp = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,30}$/;

form.addEventListener("submit", (event) => {
    const loginTest = loginRegExp.test(login.value);
    const passwordTest = passwordRegExp.test(password.value);
    if (!loginTest || !passwordTest) {
        event.preventDefault();
        validationErrorMessage.innerText = validationErrorString;
        return false;
    }
    validationErrorMessage.innerText = "";
    return true;
});
