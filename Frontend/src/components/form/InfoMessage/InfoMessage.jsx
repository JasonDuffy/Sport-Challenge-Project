import "./InfoMessage.css";

/**
 * @author Robin Hackh
 */

export function showErrorMessage(message){
  document.getElementById("form_info_container").classList.remove("success");
  document.getElementById("form_info_container").classList.add("error");
  document.getElementById("form_info_message").innerText = message;

  document.getElementById("form_info_container").scrollIntoView({ block: "end" });
}

export function showSuccessMessage(message){
  document.getElementById("form_info_container").classList.remove("error");
  document.getElementById("form_info_container").classList.add("success");
  document.getElementById("form_info_message").innerText = message;

  document.getElementById("form_info_container").scrollIntoView({ block: "end" });
}

export function hideInfoMessage(){
  document.getElementById("form_info_container").classList.remove("error");
  document.getElementById("form_info_container").classList.remove("success");
  document.getElementById("form_info_message").innerText = "";
};

function InfoMessage() {
  return (
    <div id="form_info_container" className={"pd_1 mg_b_2"}>
      <span id="form_info_message"></span>
    </div>
  );
}

export default InfoMessage;