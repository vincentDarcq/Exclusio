var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
import { debounce, disableArrow, enableArrow, loaderLeft, loaderRight, searchMovieInGlobal } from "./index.js";
import { filmGenres } from "./films-genres.js";
let allMovies = [];
let allActors = [];
let allDirectors = [];
let genresToInclude = [];
let genresToExclude = [];
let actorsToFilter = [];
let directorsToFilter = [];
let filterMode = "exclusion";
let indexFilterActors = 0;
let indexFilterDirectors = 0;
let directorsElements = document.querySelector('.directors');
let actorsElements = document.querySelector('.actors');
let indexGenresFilms = 3;
const caroussels = document.querySelector('.carousels');
const loader = document.createElement('i');
loader.classList.add('fa', 'fa-2x', 'fa-spinner', 'spinner', 'margin-auto');
const arrowDownFilmActor = document.createElement('span');
arrowDownFilmActor.classList.add('fa', 'fa-2x', 'fa-arrow-circle-down');
const arrowDownFilmReal = document.createElement('span');
arrowDownFilmReal.classList.add('fa', 'fa-2x', 'fa-arrow-circle-down');
const arrowLeft = document.querySelector('.fa-arrow-circle-left');
const arrowRight = document.querySelector('.fa-arrow-circle-right');
switchFilmsTabFilters('genres');
fetchActors();
fetchDirectors();
fetchFilms(0);
export function switchMode() {
    filterMode = filterMode === "exclusion" ? "inclusion" : "exclusion";
    const button = document.querySelector('.filter-mode');
    button.innerHTML = `Passer en mode ${filterMode === "exclusion" ? "inclusion" : "exclusion"}`;
    const genresTab = document.querySelector('.tab-genres');
    const directorsTab = document.querySelector('.tab-directors');
    const actorsTab = document.querySelector('.tab-actors');
    const modeForTab = `${filterMode.charAt(0).toUpperCase()}${filterMode.substring(1)}`;
    genresTab.innerHTML = `${modeForTab} des genres`;
    directorsTab.innerHTML = `${modeForTab} des réalisateurs`;
    actorsTab.innerHTML = `${modeForTab} des acteurs`;
}
export function switchFilmsTabFilters(tab) {
    return __awaiter(this, void 0, void 0, function* () {
        indexFilterActors = 0;
        indexFilterDirectors = 0;
        ['genres', 'directors', 'actors'].filter(t => t !== tab).forEach(tab => {
            const tabEl = document.querySelector(`.tab-${tab}`);
            tabEl.classList.remove('active');
            const exclusionEl = document.querySelector(`.${tab}`);
            exclusionEl.style.display = 'none';
        });
        const exclusionsTab = document.querySelector(`.tab-${tab}`);
        exclusionsTab.classList.add('active');
        const exclusions = document.querySelector(`.${tab}`);
        exclusions.style.display = 'flex';
        if (tab !== 'genres')
            yield fillFilters(tab, document.querySelector(`.list-${tab}`));
    });
}
function fillFilters(tab, element, elementsSearched) {
    return __awaiter(this, void 0, void 0, function* () {
        let html;
        switch (tab) {
            case 'actors':
                actorsElements.replaceChild(loader, actorsElements.children[2]);
                const actorFiltered = yield Promise.all(actorsToFilter.map((actor) => __awaiter(this, void 0, void 0, function* () { return getPersonElement('actor', actor, true); })));
                html = yield Promise.all(elementsSearched ?
                    elementsSearched.filter(a => !actorsToFilter.includes(a)).slice(0, 27).map((actor) => __awaiter(this, void 0, void 0, function* () { return getPersonElement('actor', actor); })) : allActors.filter(a => !actorsToFilter.includes(a)).slice(0, 27).map((actor) => __awaiter(this, void 0, void 0, function* () { return getPersonElement('actor', actor); })));
                element.innerHTML = `${actorFiltered.join('')}${html.join('')}`;
                arrowDownFilmActor.onclick = moreActors;
                actorsElements.replaceChild(arrowDownFilmActor, actorsElements.children[2]);
                break;
            case 'directors':
                directorsElements.replaceChild(loader, directorsElements.children[2]);
                const directorFiltered = yield Promise.all(directorsToFilter.map((director) => __awaiter(this, void 0, void 0, function* () { return getPersonElement('director', director, true); })));
                html = yield Promise.all(elementsSearched ?
                    elementsSearched.filter(d => !directorsToFilter.includes(d)).slice(0, 27).map((director) => __awaiter(this, void 0, void 0, function* () { return getPersonElement('director', director); })) : allDirectors.filter(d => !directorsToFilter.includes(d)).slice(0, 27).map((director) => __awaiter(this, void 0, void 0, function* () { return getPersonElement('director', director); })));
                element.innerHTML = `${directorFiltered.join('')}${html.join('')}`;
                arrowDownFilmReal.onclick = moreDirectors;
                directorsElements.replaceChild(arrowDownFilmReal, directorsElements.children[2]);
        }
    });
}
function getPersonElement(type_1, person_1) {
    return __awaiter(this, arguments, void 0, function* (type, person, filter = false) {
        const selector = person.replace(/\s+/g, '');
        const i = type === 'actor' ? actorsToFilter.findIndex(a => a === person) : directorsToFilter.findIndex(a => a === person);
        ;
        const picture = yield fetchPersonPicture(person);
        return `
    <div class="element flex-column"
      ${type === 'actor' ? `onclick="actorSelected('${selector}')"` : `onclick="directorSelected('${selector}')"`}
    >
      ${picture ? `<img src="${picture}" alt="actor">` : ''}
      <span class="
          ${selector} 
          ${i !== -1 ? 'element-selected' : ''} 
          margin-auto fit-content
          ${filter ? `${filterMode === "exclusion" ? 'exclude' : 'include'}` : ''}"
      >
        ${person}
      </span>
    </div>
  `;
    });
}
function moreActors() {
    return __awaiter(this, void 0, void 0, function* () {
        indexFilterActors += 28;
        actorsElements.replaceChild(loader, actorsElements.children[2]);
        const actors = document.querySelector(`.list-actors`);
        const html = yield Promise.all(allActors.slice(indexFilterActors, indexFilterActors + 27).map((actor) => __awaiter(this, void 0, void 0, function* () {
            const selector = actor.replace(/\s+/g, '');
            const i = actorsToFilter.findIndex(a => a === actor);
            const picture = yield fetchPersonPicture(actor);
            return `
          <div class="
              ${selector}
              element flex-column"
            onclick="actorSelected('${selector}')"
          >
            ${picture ? `<img src="${picture}" alt="actor">` : ''}
            <span class="${i !== -1 ? 'element-selected' : ''} margin-auto">${actor}</span>
          </div>
        `;
        })));
        actors.innerHTML = `${actors.innerHTML}${html.join('')}`;
        actorsElements.replaceChild(arrowDownFilmActor, actorsElements.children[2]);
    });
}
function moreDirectors() {
    return __awaiter(this, void 0, void 0, function* () {
        indexFilterDirectors += 28;
        directorsElements.replaceChild(loader, directorsElements.children[2]);
        const actors = document.querySelector(`.list-directors`);
        const html = yield Promise.all(allDirectors.slice(indexFilterDirectors, indexFilterDirectors + 27).map((director) => __awaiter(this, void 0, void 0, function* () {
            const selector = director.replace(/\s+/g, '');
            const i = directorsToFilter.findIndex(a => a === director);
            const picture = yield fetchPersonPicture(director);
            return `
          <div class="
              ${selector}
              element flex-column"
            onclick="directorSelected('${selector}')"
          >
            ${picture ? `<img src= "${picture}" alt = "director" >` : ''}
            <span class=" ${i !== -1 ? 'element-selected' : ''} margin-auto">${director}</span>
          </div>
        `;
        })));
        actors.innerHTML = `${actors.innerHTML}${html.join('')}`;
        directorsElements.replaceChild(arrowDownFilmReal, directorsElements.children[2]);
    });
}
function fetchPersonPicture(person) {
    return __awaiter(this, void 0, void 0, function* () {
        const stringPerson = person.replace(' ', '%20');
        const searchPerson = yield fetch(`allo/${stringPerson}`);
        const resultPerson = yield searchPerson.json();
        return resultPerson[0].data.thumbnail;
    });
}
function searchActor() {
    return __awaiter(this, void 0, void 0, function* () {
        const input = document.querySelector('input.searchActor');
        const actors = allActors.filter(actor => actor.toLowerCase().includes(input.value.toLowerCase()));
        yield fillFilters('actors', document.querySelector('.list-actors'), actors);
    });
}
function searchDirector() {
    return __awaiter(this, void 0, void 0, function* () {
        const input = document.querySelector('input.searchDirector');
        const directors = allDirectors.filter(director => director.toLowerCase().includes(input.value.toLowerCase()));
        yield fillFilters('directors', document.querySelector('.list-directors'), directors);
    });
}
export const actorChange = debounce(() => searchActor());
export const directorChange = debounce(() => searchDirector());
export function genreSelected(checkbox) {
    const element = document.querySelector(`.${checkbox.value}`);
    if (checkbox.checked) {
        element.classList.add(`${filterMode === "exclusion" ? 'exclude' : 'include'}`);
    }
    else {
        element.classList.remove(`${filterMode === "exclusion" ? 'exclude' : 'include'}`);
    }
    if (checkbox.checked) {
        genresToExclude.push(checkbox.value);
    }
    else {
        genresToExclude.splice(genresToExclude.findIndex(g => g === checkbox.value), 1);
    }
    fetchFilms(0);
}
function addNextGenreElement() {
    const container = document.querySelector('.filters-elements');
    const child = document.createElement("div");
    child.classList.add("element", "flex-column", filmGenres[indexGenresFilms].text, "slide-in");
    child.innerHTML = `
    <p class="element-text">${filmGenres[indexGenresFilms].text}</p>
    <div class="icons-selection flex">
        <svg class="red-cross" onclick="elementGenreSelected('${filmGenres[indexGenresFilms].enum}', false)" xmlns="http://www.w3.org/2000/svg" clip-rule="evenodd" fill-rule="evenodd" image-rendering="optimizeQuality" shape-rendering="geometricPrecision" text-rendering="geometricPrecision" viewBox="0 0 21.0002 21.0002">
            <g id="图层_x0020_1"><path d="m0 0h21v21h-21z" fill="none"/>
                <g id="_208846520" fill="#f00">
                    <path id="_209133816" d="m3.48583 16.0285c4.20248-4.20232 8.20736-8.2072 12.4097-12.4097.0711-.0711.18718-.0711.25828 0l.32657.32657.00331.00331.70259.70259.00314.00331.32657.32657c.0711.07094.0711.18702 0 .25812-4.20248 4.20248-8.2072 8.2072-12.4097 12.4097-.0711.0711-.18718.0711-.25812 0l-.32657-.32657-.00331-.00314-.70259-.70259-.00331-.00331-.32657-.32657c-.0711-.0711-.0711-.18718 0-.25828l.00001-.00001z"/><path id="_109820464" d="m15.8957 17.6489c-4.20248-4.20248-8.2072-8.2072-12.4097-12.4097-.0711-.0711-.0711-.18718 0-.25812l.32657-.32657.00314-.00331.70259-.70259.00331-.00331.32657-.32657c.0711-.0711.18718-.0711.25828 0 4.20232 4.20248 8.2072 8.20736 12.4097 12.4097.0711.0711.0711.18718 0 .25828l-.32657.32657-.00331.00331-.70259.70259-.00331.00314-.32657.32657c-.07094.0711-.18702.0711-.25812 0z"/>
                </g>
            </g>
        </svg>
        <p>Comédie</p>
        <svg class="green-validate" onclick="elementGenreSelected('${filmGenres[indexGenresFilms].enum}', true)" xmlns="http://www.w3.org/2000/svg" id="Layer_1" enable-background="new 0 0 700 700" height="700" viewBox="0 0 700 700" width="700">
            <circle cx="350" cy="350" fill="#6dc152" r="350"/>
            <path d="m294.143 548.471-171.298-171.298 69.298-69.296 102 101.999 215.086-215.086 69.296 69.296z" fill="#fff"/>
        </svg>
    </div>
  `;
    child.addEventListener("animationend", () => {
        child.classList.remove("slide-in");
    });
    container.appendChild(child);
    indexGenresFilms++;
}
export function elementGenreSelected(genreEnum, validate) {
    const genre = filmGenres.find(g => g.enum === genreEnum);
    if (validate)
        genresToInclude.push(genre.enum);
    else
        genresToExclude.push(genre.enum);
    fetchFilms(0);
    const element = document.querySelector(`.${genre === null || genre === void 0 ? void 0 : genre.text}`);
    element.classList.add('fade-out');
    setTimeout(() => {
        element.remove();
        addNextGenreElement();
    }, 300);
}
export function actorSelected(actor) {
    const indexActor = allActors.map(a => a.replace(/\s+/g, '')).findIndex(a => a === actor);
    const element = document.querySelector(`.${actor}`);
    const actorChecked = actorsToFilter.map(a => a.replace(/\s+/g, '')).includes(actor);
    if (actorChecked) {
        element.classList.remove(`${filterMode === "exclusion" ? 'exclude' : 'include'}`);
        actorsToFilter.splice(actorsToFilter.findIndex(a => a === allActors[indexActor]), 1);
    }
    else {
        element.classList.add(`${filterMode === "exclusion" ? 'exclude' : 'include'}`);
        actorsToFilter.push(allActors[indexActor]);
    }
    fetchFilms(0);
}
export function directorSelected(director) {
    const indexDirector = allDirectors.map(d => d.replace(/\s+/g, '')).findIndex(d => d === director);
    const element = document.querySelector(`.${director}`);
    const actorChecked = directorsToFilter.map(a => a.replace(/\s+/g, '')).includes(director);
    if (actorChecked) {
        element.classList.remove(`${filterMode === "exclusion" ? 'exclude' : 'include'}`);
        directorsToFilter.splice(directorsToFilter.findIndex(d => d === allDirectors[indexDirector]), 1);
    }
    else {
        element.classList.add(`${filterMode === "exclusion" ? 'exclude' : 'include'}`);
        directorsToFilter.push(allDirectors[indexDirector]);
    }
    fetchFilms(0);
}
export function selectMovie(id) {
    return __awaiter(this, void 0, void 0, function* () {
        let movie = allMovies.find(movie => movie.id === parseInt(id));
        if (!movie)
            movie = searchMovieInGlobal(id);
        const popup = document.querySelector('.film-popup');
        const emptyCov = movie === null || movie === void 0 ? void 0 : movie.covPortrait.indexOf('empty');
        popup.innerHTML = `
    <div class="flex header">
      <h2 class="film-title">${movie === null || movie === void 0 ? void 0 : movie.titre}</h2>
      <div class="cross" onClick="closePopup()">X</div>
    </div>
    <div class="flex infos">
      <div class="flex-column">
        <h4>Genres : ${movie === null || movie === void 0 ? void 0 : movie.genre}</h4>
        <h4>Casting : ${movie === null || movie === void 0 ? void 0 : movie.casting}</h4>
        <h4>Réalisateur: ${movie === null || movie === void 0 ? void 0 : movie.realisateur}</h4>
        <h4>Durée: ${movie === null || movie === void 0 ? void 0 : movie.time}</h4>
        <h4>Année: ${movie === null || movie === void 0 ? void 0 : movie.year}</h4>
      </div>
      ${emptyCov === -1 ? `<img src="${movie === null || movie === void 0 ? void 0 : movie.covPortrait}" alt="cov" >` : ''}
    </div>
    ${(movie === null || movie === void 0 ? void 0 : movie.synopsis) ? `<p>${movie === null || movie === void 0 ? void 0 : movie.synopsis} </p>` : ''}
    <div class="flex notes">
      ${(movie === null || movie === void 0 ? void 0 : movie.alloGrade) ? `<span>Note allociné: ${movie === null || movie === void 0 ? void 0 : movie.alloGrade}/5</span >` : ''}
      ${(movie === null || movie === void 0 ? void 0 : movie.imdbGrade) ? `<span>Note Imdb: ${movie.imdbGrade}/10</span>` : ''}
    </div>
  `;
        popup.style.display = 'block';
    });
}
export function closePopup() {
    const popup = document.querySelector('.film-popup');
    popup.style.display = 'none';
}
function fetchActors() {
    return __awaiter(this, void 0, void 0, function* () {
        const result = yield fetch(`movies/acteurs`);
        const content = yield result.json();
        allActors = content;
    });
}
function fetchDirectors() {
    return __awaiter(this, void 0, void 0, function* () {
        const result = yield fetch(`movies/realisateurs`);
        const content = yield result.json();
        allDirectors = content;
    });
}
export function navigate(direction, page) {
    return __awaiter(this, void 0, void 0, function* () {
        fetchFilms(page, direction);
    });
}
function fetchFilms(page, direction) {
    return __awaiter(this, void 0, void 0, function* () {
        if (direction)
            caroussels.replaceChild(direction === 'right' ? loaderRight : loaderLeft, caroussels.children[direction === 'right' ? 2 : 0]);
        const result = yield fetch(`movies/filters/${page}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                genresToExclude: genresToExclude,
                genresToInclude: genresToInclude,
                castingToExclude: actorsToFilter,
                castingToInclude: actorsToFilter,
                realisateursToExclude: directorsToFilter,
                realisateursToInclude: directorsToFilter
            })
        });
        const content = yield result.json();
        const movies = content.content;
        if (content.last) {
            disableArrow(arrowRight);
        }
        else {
            enableArrow(arrowRight, () => __awaiter(this, void 0, void 0, function* () {
                yield navigate('right', page + 1);
            }));
        }
        if (page > 0) {
            enableArrow(arrowLeft, () => __awaiter(this, void 0, void 0, function* () {
                yield navigate('left', page - 1);
            }));
        }
        else {
            disableArrow(arrowLeft);
        }
        fillLine(movies.slice(0, 8), 1, direction);
        fillLine(movies.slice(8, 16), 2, direction);
        fillLine(movies.slice(16), 3, direction);
        if (direction)
            caroussels.replaceChild(direction === 'right' ? arrowRight : arrowLeft, caroussels.children[direction === 'right' ? 2 : 0]);
    });
}
function fillLine(movies, line, direction) {
    let html = '';
    movies.forEach(movie => {
        if (allMovies.findIndex(m => m.id === movie.id) === -1) {
            allMovies.push(movie);
        }
        const emptyCov = movie.covPortrait.indexOf('empty');
        html += `
        ${emptyCov === -1 ? `<img class="img-movie" src="${movie.covPortrait}" alt="${movie.titre}" onclick="selectMovie(${movie.id})" >`
            : `<span class="titre-no-cov" onclick="selectMovie(${movie.id})">${movie.titre}</span>`}
      `;
    });
    if (direction) {
        slide(direction, line, html);
    }
    else {
        const slide = document.querySelector(`.films_${line}`);
        slide.innerHTML = html;
    }
}
function slide(direction, line, content) {
    const container = document.querySelector('.lines');
    const newSlide = document.createElement('div');
    newSlide.classList.add('flex', `films_${line}`, `enter-${direction}`);
    newSlide.innerHTML = content;
    const oldSlide = document.querySelector(`.films_${line}`);
    oldSlide.classList.add(`leave-${direction === 'right' ? 'left' : 'right'}`);
    oldSlide.addEventListener('animationend', () => {
        oldSlide.remove();
    }, { once: true });
    container.insertBefore(newSlide, container.children[line]);
    updateTopPositions();
}
window.closePopup = closePopup;
window.selectMovie = selectMovie;
window.switchFilmsTabFilters = switchFilmsTabFilters;
window.navigate = navigate;
window.genreSelected = genreSelected;
window.directorSelected = directorSelected;
window.actorSelected = actorSelected;
window.actorChange = actorChange;
window.directorChange = directorChange;
window.switchMode = switchMode;
window.moreActors = moreActors;
window.moreDirectors = moreDirectors;
window.elementGenreSelected = elementGenreSelected;
function updateTopPositions() {
    const viewportWidth = window.innerWidth;
    const films2 = document.querySelector('.films_2');
    const films3 = document.querySelector('.films_3');
    if (films2 && films3) {
        films2.style.top = `${viewportWidth * 0.12}px`;
        films3.style.top = `${viewportWidth * 0.25}px`;
    }
}
// Appelle la fonction au chargement et au redimensionnement
window.addEventListener('load', updateTopPositions);
window.addEventListener('resize', updateTopPositions);
