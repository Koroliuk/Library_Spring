function chooseType() {
    const orderType = document.getElementById("orderType").value;
    document.getElementById('startDate').valueAsDate = new Date();
    document.getElementById('endDate').valueAsDate = new Date();
    document.getElementById('endDate').disabled = orderType === 'readingHole';
}

Date.prototype.withoutTime = function () {
    const date = new Date(this);
    date.setHours(0, 0, 0, 0);
    return date;
}

const titleValidationMessage1 = 'Заголовок має містити хоча б 2 та не більше 50 символів';
const titleValidationMessage2 = 'Заголовок не може містити хоча б один непробільний символ';
const authorsValidatorMessage = 'Імена авторів мають розділятись комою';
const descriptionValidationMessage = 'Опис може містити до 1000 символів';
const bookLanguageValidationMessage = 'Поле може мітити до 30 символів';
const editionValidationMessage = 'Назва видання має мати не більше 30 символів';
const publicationDateValidationMessage = 'Дата видання має бути не пізніше сьогодні';
const priceValidationMessage = 'Ціна має бути числом, яке більше нуля';
const countValidationMessage = 'Кількість екземплярів має бути цілим числом, яке більше нуля';

const form = document.getElementById('form');
const titleUa = document.getElementById('titleUa');
const authorsUa = document.getElementById('authorsUa');
const descriptionUa = document.getElementById("descriptionUa");
const bookLanguageUa = document.getElementById("bookLanguageUa");
const editionUa = document.getElementById('editionUa');
const publicationDateUa = document.getElementById('publicationDateUa');
const titleEn = document.getElementById('titleEn');
const authorsEn = document.getElementById('authorsEn');
const descriptionEn = document.getElementById("descriptionEn");
const bookLanguageEn = document.getElementById("bookLanguageEn");
const editionEn = document.getElementById('editionEn');
const publicationDateEn = document.getElementById('publicationDateEn');
const price = document.getElementById('price');
const count = document.getElementById('count');

const titleMessage = document.getElementById('titleMessage');
const authorsMessage = document.getElementById('authorsMessage');
const descriptionMessage = document.getElementById('descriptionMessage');
const bookLanguageMessage = document.getElementById('bookLanguageMessage');
const editionMessage = document.getElementById('editionMessage');
const publicationDateMessage = document.getElementById('publicationDateMessage');
const priceMessage = document.getElementById('priceMessage');
const countMessage = document.getElementById('countMessage');

const titleRegExp = /^[\S][\S ]{1,49}$/;
const authorsRegExp = /^(?<!,)(([A-zА-яЄєЇїІі]\.(?!\.)){0,2}([A-zА-яЄєЇїІі]{1,20}))((?<!,),([A-zА-яЄєЇїІі]\.(?!\.)){0,2}([A-zА-яЄєЇїІі]{1,20}))*((?<=,),([A-zА-яЄєЇїІі]\.(?!\.)){0,2}([A-z]{1,20})(?!,))?$/;
const descriptionRegExp = /^.{0,1000}$/;
const bookLanguageRegExp = /^[A-zА-яЄєЇїІі]{1,30}$/;
const editionRegExp = /^.{1,30}$/;
const priceRegExp = /^[0-9]+\.?[0-9]+$/;
const countRegExp = /^[0-9]+$/;

function titleListener(title) {
    const titleTest = titleRegExp.test(title.value);
    if (titleTest) {
        titleMessage.innerText = "";
    } else {
        const titleStrip = title.value.trim();
        if (titleStrip === '') {
            titleMessage.innerText = titleValidationMessage2;
        } else {
            titleMessage.innerText = titleValidationMessage1;
        }
    }
}

function authorsListener(authors) {
    const authorsTest = authorsRegExp.test(authors.value);
    if (authorsTest) {
        authorsMessage.innerText = "";
    } else {
        authorsMessage.innerText = authorsValidatorMessage;
    }
}

function descriptionListener(description) {
    const descriptionTest = descriptionRegExp.test(description.value);
    if (descriptionTest) {
        descriptionMessage.innerText = "";
    } else {
        descriptionMessage.innerText = descriptionValidationMessage;
    }
}

function bookLanguageListener(bookLanguage) {
    const languageTest = bookLanguageRegExp.test(bookLanguage.value);
    if (languageTest) {
        bookLanguageMessage.innerText = "";
    } else {
        bookLanguageMessage.innerText = bookLanguageValidationMessage;
    }
}

function editionListener(edition) {
    const editionTest = editionRegExp.test(edition.value);
    if (editionTest) {
        editionMessage.innerText = "";
    } else {
        editionMessage.innerText = editionValidationMessage;
    }
}

function publicationDateListener(publicationDate) {
    const now = new Date().withoutTime();
    const date = new Date(publicationDate.value).withoutTime();
    const dateTest = date > now;
    if (dateTest) {
        publicationDateMessage.innerText = publicationDateValidationMessage;
    } else {
        publicationDateMessage.innerText = "";
    }
}

titleUa.addEventListener("input", () => titleListener(titleUa));
titleEn.addEventListener("input", () => titleListener(titleEn));
authorsUa.addEventListener("input", () => authorsListener(authorsUa));
authorsEn.addEventListener("input", () => authorsListener(authorsEn));
descriptionUa.addEventListener('input', () => descriptionListener(descriptionUa));
descriptionEn.addEventListener('input', () => descriptionListener(descriptionEn));
bookLanguageUa.addEventListener('input', () => bookLanguageListener(bookLanguageUa));
bookLanguageEn.addEventListener('input', () => bookLanguageListener(bookLanguageEn));
editionUa.addEventListener('input', () => editionListener(editionUa));
editionEn.addEventListener('input', () => editionListener(editionEn));
publicationDateUa.addEventListener('input', () => publicationDateListener(publicationDateUa));
publicationDateEn.addEventListener('input', () => publicationDateListener(publicationDateEn));

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

count.addEventListener('input', () => {
    const countTest = countRegExp.test(count.value);
    if (countTest) {
        if (count.value > 0) {
            countMessage.innerText = "";
        } else {
            countMessage.innerText = countValidationMessage;
        }
    } else {
        countMessage.innerText = countValidationMessage;
    }
});

form.addEventListener("submit", (event) => {
    const titleTest = titleRegExp.test(titleUa.value);
    const authorsTest = authorsRegExp.test(authorsUa.value);
    const descriptionTest = descriptionRegExp.test(descriptionUa.value);
    const languageTest = bookLanguageRegExp.test(bookLanguageUa.value);
    const editionTest = editionRegExp.test(editionUa.value);
    const now = new Date().withoutTime();
    const date = new Date(publicationDateUa.value).withoutTime();
    const dateTest = date > now;
    const priceTest = priceRegExp.test(price.value);
    const countTest = countRegExp.test(count.value);

    if (!titleTest || !authorsTest || !descriptionTest || !languageTest
        || !editionTest || dateTest || !priceTest || !countTest) {
        event.preventDefault();
        return false;
    }
    return true;
});