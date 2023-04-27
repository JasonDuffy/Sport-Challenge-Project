import $ from "jquery";

$(document).ready(function () {
  $("#top_nav_management").on("click", function () {
    $("#top_nav_management")
      .children("#top_nav_management_menu")
      .slideToggle(200);
  });

  $(".mobile_nav_icon").on("click", function () {
    $(".mobile_nav_bars").toggleClass("open");
    $(".mobile_nav").toggleClass("mobile_nav_open");
    $(".moible_nav_close").toggleClass("mobile_nav_close_show");
  });

  $(".moible_nav_close").on("click", function () {
    $(".mobile_nav_bars").toggleClass("open");
    $(".mobile_nav").toggleClass("mobile_nav_open");
    $(".moible_nav_close").toggleClass("mobile_nav_close_show");
  });

  $(".mobile_nav_dropdown").on("click", function () {
      $(".mobile_nav_list_item").children(".mobile_nav_dropdown_menu").slideToggle(200);
  });
});
