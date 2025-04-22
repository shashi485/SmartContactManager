console.log("Script file loaded!");

// Toggle Sidebar Function
const toggleSidebar = () => {
    let sidebar = $("#sidebar");  // Use ID for better specificity
    let content = $(".content");

    if (sidebar.hasClass("visible")) {
        console.log("Closing sidebar...");
        sidebar.removeClass("visible").css("display", "none");
        content.css("margin-left", "0%");
    } else {
        console.log("Opening sidebar...");
        sidebar.addClass("visible").css("display", "block");
        content.css("margin-left", "20%");
    }
};

// Ensure script runs after DOM is fully loaded
$(document).ready(function () {
    console.log("Document is ready!");

    // Attach event to the cross button (×)
    $("#closeSidebar").click(function () {
        console.log("Cross button clicked!");
        toggleSidebar();
    });

    // Attach event to the menu (☰)
    $("#menuToggle").click(function () {
        console.log("Hamburger button clicked!");
        toggleSidebar();
    });
});
