document.addEventListener("DOMContentLoaded", function () {
    // smooth scroll to sections
    const links = document.querySelectorAll("a[href^='#']");
    links.forEach(link => {
        link.addEventListener("click", function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute("href"));
            if (target) {
                window.scrollTo({
                    top: target.offsetTop,
                    behavior: "smooth"
                });
            }
        });
    });

    // animate cards on hover
    const cards = document.querySelectorAll(".card");
    cards.forEach(card => {
        card.addEventListener("mouseover", function () {
            this.style.transform = "translateY(-5px)";
        });
        card.addEventListener("mouseout", function () {
            this.style.transform = "translateY(0)";
        });
    });
});

document.addEventListener("DOMContentLoaded", function () {
    // add animation to table rows
    const rows = document.querySelectorAll("table tbody tr");
    rows.forEach(row => {
        row.addEventListener("mouseover", () => {
            row.style.backgroundColor = "#444";
        });
        row.addEventListener("mouseout", () => {
            row.style.backgroundColor = "";
        });
    });

    // smooth form validation scroll
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