import { showErrorMessage, showSuccessMessage } from "../../components/form/InfoMessage/InfoMessage";
import apiFetch from "../../utils/api";

/**
 * @author Robin Hackh
 */

export async function fetchSportData(sportID) {
  let apiResponse, sportResData;
  apiResponse = await apiFetch("/sports/" + sportID + "/", "GET", {}, null);

  if (apiResponse.error === false) {
    sportResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  return sportResData;
}

export function checkSportInput(sportName, sportFactor) {
  if (sportName === "") {
    showErrorMessage("Bitte gebe deiner Sportart einen Namen.");
    return false;
  }

  if (sportFactor < 0.1) {
    showErrorMessage("Der Faktor deiner Sportart muss 0.1 oder größer sein.");
    return false;
  }

  return true;
}

export async function saveOrUpdateSport(sportID, sportObj, action) {
  let apiResponse, sportResData;

  if (action === "add") {
    apiResponse = await apiFetch("/sports/", "POST", { Accept: "application/json", "Content-Type": "application/json" }, JSON.stringify(sportObj));
  } else {
    apiResponse = await apiFetch("/sports/" + sportID + "/", "PUT", { Accept: "application/json", "Content-Type": "application/json" }, JSON.stringify(sportObj));
  }

  if (apiResponse.error === false) {
    sportResData = apiResponse.resData;
    showSuccessMessage("Die Sportart wurde erfolgreich gespeichert.");
  } else {
    showErrorMessage("Beim Speichern der Sportart ist ein Fehler aufgetreten! " + apiResponse.status);
    return;
  }

  return sportResData;
}
