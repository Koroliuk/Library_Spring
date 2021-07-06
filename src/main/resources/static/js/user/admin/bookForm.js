Date.prototype.withoutTime = function () {
    const date = new Date(this);
    date.setHours(0, 0, 0, 0);
    return date;
}

const form = document.getElementById('form');
const titleUa = document.getElementById('titleUa');
const authorsUa = document.getElementById('authorsUa');
const descriptionUa = document.getElementById("descriptionUa");
const bookLanguageUa = document.getElementById("bookLanguageUa");
const editionUa = document.getElementById('editionUa');
const titleEn = document.getElementById('titleEn');
const authorsEn = document.getElementById('authorsEn');
const descriptionEn = document.getElementById("descriptionEn");
const bookLanguageEn = document.getElementById("bookLanguageEn");
const editionEn = document.getElementById('editionEn');
const publicationDate = document.getElementById('publicationDate');
const price = document.getElementById('price');
const amount = document.getElementById('amount');

const totalMessage = document.getElementById('totalMessage');
const titleMessage = document.getElementById('titleMessage');
const authorsMessage = document.getElementById('authorsMessage');
const descriptionMessage = document.getElementById('descriptionMessage');
const bookLanguageMessage = document.getElementById('bookLanguageMessage');
const editionMessage = document.getElementById('editionMessage');
const publicationDateMessage = document.getElementById('publicationDateMessage');
const priceMessage = document.getElementById('priceMessage');
const amountMessage = document.getElementById('amountMessage');

const titleRegExp = /^[\S][\S ]{1,100}$/;
const authorsRegExp = /^.{2,200}$/;
const descriptionRegExp = /^.{2,1000}$/;
const bookLanguageRegExp = /^[A-zА-яЄєЇїІі]{1,30}$/;
const editionRegExp = /^.{2,50}$/;
const priceRegExp = /^[0-9]*\.?[0-9]+$/;
const countRegExp = /^[0-9]+$/;

function titlesListener(title1, title2) {
    const titleTest1 = titleRegExp.test(title1.value);
    const titleTest2 = titleRegExp.test(title2.value);
    if (titleTest1 && titleTest2) {
        titleMessage.innerText = "";
    } else {
        const titleStrip1 = title1.value.trim();
        const titleStrip2 = title2.value.trim();
        if (titleStrip1 === '' || titleStrip2 === '') {
            titleMessage.innerText = titleValidationMessage2;
        } else {
            titleMessage.innerText = titleValidationMessage1;
        }
    }
}

function authorsListener(authors1, authors2) {
    const authorsTest1 = authorsRegExp.test(authors1.value);
    const authorsTest2 = authorsRegExp.test(authors2.value);
    if (authorsTest1 && authorsTest2) {
        authorsMessage.innerText = "";
    } else {
        authorsMessage.innerText = authorsValidatorMessage;
    }
}

function descriptionsListener(description1, description2) {
    const descriptionTest1 = descriptionRegExp.test(description1.value);
    const descriptionTest2 = descriptionRegExp.test(description2.value);
    if (descriptionTest1 && descriptionTest2) {
        descriptionMessage.innerText = "";
    } else {
        descriptionMessage.innerText = descriptionValidationMessage;
    }
}

function bookLanguagesListener(bookLanguage1, bookLanguage2) {
    const languageTest1 = bookLanguageRegExp.test(bookLanguage1.value);
    const languageTest2 = bookLanguageRegExp.test(bookLanguage2.value);
    if (languageTest1 && languageTest2) {
        bookLanguageMessage.innerText = "";
    } else {
        bookLanguageMessage.innerText = bookLanguageValidationMessage;
    }
}

function editionsListener(edition1, edition2) {
    const editionTest1 = editionRegExp.test(edition1.value);
    const editionTest2 = editionRegExp.test(edition2.value);
    if (editionTest1 && editionTest2) {
        editionMessage.innerText = "";
    } else {
        editionMessage.innerText = editionValidationMessage;
    }
}

titleUa.addEventListener("input", () => titlesListener(titleUa, titleEn));
titleEn.addEventListener("input", () => titlesListener(titleUa, titleEn));
authorsUa.addEventListener("input", () => authorsListener(authorsUa, authorsEn));
authorsEn.addEventListener("input", () => authorsListener(authorsUa, authorsEn));
descriptionUa.addEventListener('input', () => descriptionsListener(descriptionUa, descriptionEn));
descriptionEn.addEventListener('input', () => descriptionsListener(descriptionUa, descriptionEn));
bookLanguageUa.addEventListener('input', () => bookLanguagesListener(bookLanguageUa, bookLanguageEn));
bookLanguageEn.addEventListener('input', () => bookLanguagesListener(bookLanguageUa, bookLanguageEn));
editionUa.addEventListener('input', () => editionsListener(editionUa, editionEn));
editionEn.addEventListener('input', () => editionsListener(editionUa, editionEn));

publicationDate.addEventListener('input', () => {
    const now = new Date().withoutTime();
    const date = new Date(publicationDate.value).withoutTime();
    const dateTest = date > now;
    if (dateTest) {
        publicationDateMessage.innerText = publicationDateValidationMessage;
    } else {
        publicationDateMessage.innerText = "";
    }
});

price.addEventListener('input', () => {
    const priceTest = priceRegExp.test(price.value);
    if (priceTest) {
        if (price.value > 0) {
            priceMessage.innerText = "";
        } else {
            priceMessage.innerText = priceValidationMessage;
        }
    } else {
        priceMessage.innerText = priceValidationMessage;
    }
});

amount.addEventListener('input', () => {
    const countTest = countRegExp.test(amount.value);
    if (countTest) {
        if (amount.value > 0) {
            amountMessage.innerText = "";
        } else {
            amountMessage.innerText = countValidationMessage;
        }
    } else {
        amountMessage.innerText = countValidationMessage;
    }
});

form.addEventListener("submit", (event) => {
    const titleTest = titleRegExp.test(titleUa.value);
    const authorsTest = authorsRegExp.test(authorsUa.value);
    const descriptionTest = descriptionRegExp.test(descriptionUa.value);
    const languageTest = bookLanguageRegExp.test(bookLanguageUa.value);
    const editionTest = editionRegExp.test(editionUa.value);
    const now = new Date().withoutTime();
    const date = new Date(publicationDate.value).withoutTime();
    const dateTest = date > now;
    const priceTest = priceRegExp.test(price.value);
    const countTest = countRegExp.test(amount.value);

    if (!titleTest || !authorsTest || !descriptionTest || !languageTest
        || !editionTest || dateTest || !priceTest || !countTest) {
        event.preventDefault();
        totalMessage.hidden = false;
        return false;
    }
    totalMessage.hidden = true;
    return true;
});
