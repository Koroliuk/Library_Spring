const form = document.getElementById('signupForm');
const login = document.getElementById('login');
const password = document.getElementById('password');
const loginMessage = document.getElementById("loginMessage");
const passwordMessage = document.getElementById("passwordMessage");

const loginRegExp = /^(?!.*\.\.)(?!.*\.$)[^\W][\w.]{4,20}$/;
const passwordRegExp = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,30}$/;

loginMessage.className = "text-danger";
passwordMessage.className = "text-danger";

login.addEventListener("input", () => {
    const loginTest = loginRegExp.test(login.value);
    if (loginTest) {
        loginMessage.innerText = "";
    } else {
        const loginLength = login.value.length
        if (loginLength < 5 || loginLength > 20) {
            loginMessage.innerText = loginValidateMessage1;
        } else {
            loginMessage.innerText = loginValidateMessage2;
        }
    }
});

password.addEventListener("input", () => {
    const passwordTest = passwordRegExp.test(password.value);
    if (passwordTest) {
        passwordMessage.innerText = "";
    } else {
        const passwordLength = password.value.length
        if (passwordLength < 8 || passwordLength > 30) {
            passwordMessage.innerText = passwordValidateMessage1;
        } else {
            passwordMessage.innerText = passwordValidateMessage2;
        }
    }
});

form.addEventListener("submit", (event) => {
    const loginTest = loginRegExp.test(login.value);
    const passwordTest = loginRegExp.test(password.value);
    if (!loginTest) {
        event.preventDefault();
        return false;
    }
    if (!passwordTest) {
        event.preventDefault();
        return false;
    }
    return true;
});
