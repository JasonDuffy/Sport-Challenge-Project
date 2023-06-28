import apiFetch from "../../utils/api";
import { showErrorMessage, showSuccessMessage } from "../../components/form/InfoMessage/InfoMessage";

/**
 * @author Robin Hackh
 */

/**
 * Fetch needed data for the input components
 */
export async function fetchFormData(challengeID) {
  let apiResponse, challengeDropdownResData;

  if (challengeID === 0) {
    apiResponse = await apiFetch("/challenges/?type=current", "GET", {}, null);
  } else {
    apiResponse = await apiFetch("/challenges/" + challengeID + "/", "GET", {}, null);
    apiResponse.resData = [apiResponse.resData]; //Array needed to map in Dropdown
  }

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
export async function fetchSportTable(bonusID, challengeID) {
  if (challengeID == 0) return [];
  
  let apiResponse, challengeSportResData, challengeSportCheckedResData;
  let sportTableResData = [];

  apiResponse = await apiFetch("/challenges/" + challengeID + "/challenge-sports/", "GET", {}, null);

  if (apiResponse.error === false) {
    challengeSportResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  apiResponse = await apiFetch("/bonuses/" + bonusID + "/challenge-sports/", "GET", {}, null);

  if (apiResponse.error === false) {
    challengeSportCheckedResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  for (const challengeSport of challengeSportResData) {
    apiResponse = await apiFetch("/sports/" + challengeSport.sportID + "/", "GET", {}, null);

    if (apiResponse.error === false) {
      const tableObj = {
        id: challengeSport.id,
        name: apiResponse.resData.name,
      };

      for (const challengeSportChecked of challengeSportCheckedResData) {
        if (challengeSportChecked.id === challengeSport.id) {
          tableObj.checked = true;
          break;
        } else {
          tableObj.checked = false;
        }
      }

      sportTableResData.push(tableObj);
    } else {
      showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
      return;
    }
  }

  return sportTableResData;
}

/**
 * @param bonusID ID of the Bonus
 * @returns Data of the Challenge coresponding to the given bonusID
 */
export async function fetchBonusChallengeData(bonusID) {
  let apiResponse, challengeResData;

  apiResponse = await apiFetch("/bonuses/" + bonusID + "/challenge/", "GET", {}, null);

  if (apiResponse.error === false) {
    challengeResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
    return;
  }
  return challengeResData;
}

/**
 * @param bonusID ID of the Bonus
 * @returns Data of the Bonus coresponding to the given bonusID
 */
export async function fetchBonusData(bonusID) {
  let apiResponse, bonusResData;

  apiResponse = await apiFetch("/bonuses/" + bonusID + "/", "GET", {}, null);

  if (apiResponse.error === false) {
    bonusResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
    return;
  }
  return bonusResData;
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
    showErrorMessage("Bitte gebe deinem Bonus eine Beschreibung.");
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
    showErrorMessage("Bitte wähle ein Startdatum für deinen Bonus aus.");
    return false;
  }

  if (bonusEndDate === "") {
    showErrorMessage("Bitte wähle ein Enddatum für deinen Bonus aus.");
    return false;
  }

  return true;
}

/**
 * @param bonusObj Object of the bonus that should be saved
 * @param challengeSportIDs all challengeSportIDs which should be effected by the Bonus
 * @returns //If successfull data of the saved bonus otherwise nothing
 */
export async function saveOrUpdateBonus(bonusID, bonusObj, challengeSportIDs) {
  let apiResponse, bonusResData;
  let query = "?challengeSportID=" + challengeSportIDs[0];

  for (let i = 1; i < challengeSportIDs.length; i++) {
    query += "&challengeSportID=" + challengeSportIDs[i];
  }

  apiResponse = await apiFetch("/bonuses/" + bonusID + "/" + query, "PUT", { Accept: "application/json", "Content-Type": "application/json" }, JSON.stringify(bonusObj));

  if (apiResponse.error === false) {
    bonusResData = apiResponse.resData;
    showSuccessMessage("Der Bonus wurde erfolgreich gespeichert.");
  } else {
    showErrorMessage("Beim speichern des Bonuses ist ein fehler aufgetreten! " + apiResponse.status);
    return;
  }

  return bonusResData;
}
