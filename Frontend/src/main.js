import $ from "jquery";

$( document ).ready(function() {
    $( "#top_nav_management" ).on( "click", function() {
        $("#top_nav_management").children("#top_nav_management_menu").slideToggle(200);
      } );
});