document.addEventListener("DOMContentLoaded", () => {
    const toggler = document.querySelector(".navbar-toggler");
    const collapse = document.querySelector("#navbarContent");

    if (toggler && collapse) {
        toggler.addEventListener("click", () => {
            collapse.classList.toggle("show");
        });
    }
});
