import apiFetch from "../../utils/api";
import { showErrorMessage, showSuccessMessage } from "../../components/form/InfoMessage/InfoMessage";

/**
 * @author Robin Hackh
 */

/**
 * Fetch needed data for the input components
 */
export async function fetchFormData() {
  let apiResponse, challengeDropdownResData;
  apiResponse = await apiFetch("/challenges/?type=current", "GET", {}, null);

  if (apiResponse.error === false) {
    challengeDropdownResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  return { challengeDropdownResData };
}

/**
 * @param challengeID ID of the Challenge
 * @returns a obj array with challengeSportID and sportName coresponding to the given ChallengeID
 */
export async function fetchsportTable(challengeID) {
  let apiResponse, challengeSportResData;
  let sportTableResData = [];

  apiResponse = await apiFetch("/challenge-sports/challenges/" + challengeID + "/", "GET", {}, null);

  if (apiResponse.error === false) {
    challengeSportResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  for (const challengeSport of challengeSportResData) {
    apiResponse = await apiFetch("/sports/" + challengeSport.sportID + "/", "GET", {}, null);

    if (apiResponse.error === false) {
      const tableObj = {
        id: challengeSport.id,
        name: apiResponse.resData.name,
      };

      sportTableResData.push(tableObj);
    } else {
      showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
      return;
    }
  }

  return sportTableResData;
}

/**
 * Checks all inputs and shows a error message if a input is incorrect
 * @param bonusName
 * @param bonusDescription
 * @param challengeID
 * @param bonusFactor
 * @param selectedSportLength
 * @param bonusStartDate
 * @param bonusEndDate
 * @returns true if all inputs are ok false otherwise
 */
export function checkBonusInput(bonusName, bonusDescription, challengeID, bonusFactor, selectedSportLength, bonusStartDate, bonusEndDate) {
  if (bonusName === "") {
    showErrorMessage("Bitte gebe deinem Bonus einen Namen.");
    return false;
  }

  if (bonusDescription === "") {
    showErrorMessage("Bitte gebe deinem Bonus einen Beschreibung.");
    return false;
  }

  if (Number(challengeID) === 0) {
    showErrorMessage("Bitte wähle eine Challenge aus, für die der Bonus gelten soll.");
    return false;
  }

  if (bonusFactor <= 0) {
    showErrorMessage("Der Faktor für deinen Bonus muss größer als 0 sein.");
    return false;
  }

  if (selectedSportLength === 0) {
    showErrorMessage("Bitte wähle mindestens eine Sportart der Challenge aus, für die der Bonus gelten soll.");
    return false;
  }

  if (bonusStartDate === "") {
    showErrorMessage("Bitte wähle ein start Datum für deinen Bonus aus.");
    return false;
  }

  if (bonusEndDate === "") {
    showErrorMessage("Bitte wähle ein end Datum für deinen Bonus aus.");
    return false;
  }

  return true;
}

/**
 * @param bonusObj Object of the bonus that should be saved
 * @param challengeSportIDs all challengeSportIDs which should be effected by the Bonus
 * @returns //If successfull data of the saved bonus otherwise nothing
 */
export async function saveBonus(bonusObj, challengeSportIDs) {
  let apiResponse, bonusResData;
  let query = "?challengesportID=" + challengeSportIDs[0];

  console.log(bonusObj);
  console.log(challengeSportIDs);

  for (let i = 1; i < challengeSportIDs.length; i++) {
    query += "&challengesportID=" + challengeSportIDs[i];
  }
  
  console.log(query);

  apiResponse = await apiFetch("/bonuses/" + query, "POST", { Accept: "application/json", "Content-Type": "application/json" }, JSON.stringify(bonusObj));

  if (apiResponse.error === false) {
    bonusResData = apiResponse.resData;
    showSuccessMessage("Der Bonus wurde erfolgreich gespeichert.");
  } else {
    showErrorMessage("Beim speichern des Bonuses ist ein fehler aufgetreten! " + apiResponse.status);
    return;
  }

  return bonusResData;
}
