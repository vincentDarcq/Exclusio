"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
const inputId = document.querySelector('input.id');
inputId.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
        console.log('enter');
        connexion();
    }
});
const button = document.querySelector('button');
button.addEventListener('click', () => {
    connexion();
});
function connexion() {
    return __awaiter(this, void 0, void 0, function* () {
        fetch(`connexion?id=${inputId.value}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
            credentials: "include",
        }).then(reponse => {
            console.log(reponse);
            if (reponse.ok)
                window.location.href = "/";
        });
    });
}
