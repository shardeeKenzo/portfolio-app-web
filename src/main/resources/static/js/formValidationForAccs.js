document.addEventListener("DOMContentLoaded", function () {
    const forms = document.querySelectorAll(".needs-validation");

    forms.forEach(form => {
        form.addEventListener("submit", function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
                form.scrollIntoView({ behavior: "smooth", block: "center" });
            }
            form.classList.add("was-validated");
        });
    });
});
