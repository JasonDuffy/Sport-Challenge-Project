import apiFetch from "../../utils/api";
import { showErrorMessage, showSuccessMessage } from "../../components/form/InfoMessage/InfoMessage";
import "./Profile.css";

/**
 * @author Robin Hackh
 */

export async function fetchUserData() {
  let apiResponse, memberResData, imageResData;

  apiResponse = await apiFetch("/members/loggedIn/", "GET", {}, null);

  if (apiResponse.error === false) {
    memberResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  apiResponse = await apiFetch("/images/" + memberResData.imageID + "/", "GET", {}, null);

  if (apiResponse.error === false) {
    imageResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  return { memberResData, imageResData };
}

export function checkUserInput(firstName, lastName, userImage) {
  if (firstName === "") {
    showErrorMessage("Bitte gebe einen Vornamen ein.");
    return false;
  }

  if (firstName === "") {
    showErrorMessage("Bitte gebe einen Nachnamen ein.");
    return false;
  }

  if (userImage != null) {
    //Checks if the file is an image and smaller than 10MB
    if (userImage.size > 10000000) {
      showErrorMessage("Das Bild darf nicht größer als 10Mb sein.");
      return false;
    } else if (/^image/.test(userImage.type) === false) {
      showErrorMessage("Es sind nur Bilder zum hochladen erlaubt.");
      return false;
    }
  }

  return true;
}

export async function saveUser(userID, userObj, userImage, userImageID) {
  let apiResponse, memberResData, imageResData;
  let imageFetchBodyData = new FormData();

  if (userImage != null) {
    imageFetchBodyData.append("file", userImage);
    if (userImageID === 0) {
      apiResponse = await apiFetch("/images/", "POST", {}, imageFetchBodyData);
    } else {
      apiResponse = await apiFetch("/images/" + userImageID + "/", "PUT", {}, imageFetchBodyData);
    }

    if (apiResponse.error === false) {
      imageResData = apiResponse.resData;
    } else {
      showErrorMessage("Beim speichern deines Profiels ist ein fehler aufgetreten! " + apiResponse.status);
      return;
    }
  } else {
    imageResData = { id: userImageID };
  }

  userObj.imageID = imageResData.id;
  apiResponse = await apiFetch("/members/" + userID + "/", "PUT", { Accept: "application/json", "Content-Type": "application/json" }, JSON.stringify(userObj));

  if (apiResponse.error === false) {
    memberResData = apiResponse.resData;
    memberResData.image = imageResData;
    showSuccessMessage("Dein Profil wurde erfolgreich gespeichert.");
  } else {
    showErrorMessage("Beim speichern deines Profils ist ein fehler aufgetreten! " + apiResponse.status);
    return;
  }

  return memberResData;
}
