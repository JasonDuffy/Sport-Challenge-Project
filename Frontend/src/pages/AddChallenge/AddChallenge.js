import apiFetch from "../../utils/api";
import { showErrorMessage, showSuccessMessage } from "../../components/form/InfoMessage/InfoMessage";

/**
 * @author Robin Hackh
 */

/**
 * @returns a obj array with sportData
 */
export async function fetchSportTable(challengeID) {
  let apiResponse, sportResData, challengeSportResData;
  let sportDisplayArray = [];

  apiResponse = await apiFetch("/sports/", "GET", {}, null);

  if (apiResponse.error === false) {
    sportResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  apiResponse = await apiFetch("/challenges/" + challengeID + "/challenge-sports/", "GET", {}, null);

  if (apiResponse.error === false) {
    challengeSportResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  //If the sport is selected in the Challenge the specified factor
  for (const sport of sportResData) {
    sport.checked = false;

    for (const challengeSport of challengeSportResData) {
      if (sport.id === challengeSport.sportID) {
        sport.checked = true;
        sport.factor = challengeSport.factor;
        break;
      }
    }

    sportDisplayArray.push(sport);
  }

  return { sportDisplayArray };
}

/**
 * @param challengeID ID of the Challenge
 * @returns Data of the Challenge coresponding to the given challengeID
 */
export async function fetchChallengeData(challengeID) {
  let apiResponse, challengeResData;

  apiResponse = await apiFetch("/challenges/" + challengeID + "/", "GET", {}, null);

  if (apiResponse.error === false) {
    challengeResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  if(challengeResData.imageID === null) challengeResData.imageID = 0;

  return challengeResData;
}

/**
 * @param imageID ID of the Image
 * @returns Data of the Image coresponding to the given imageID
 */
export async function fetchImageData(imageID) {
  let apiResponse, imageResData;

  apiResponse = await apiFetch("/images/" + imageID + "/", "GET", {}, null);

  if (apiResponse.error === false) {
    imageResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
    return;
  }
  return imageResData;
}

export function checkChallengeInput(
  challengeName,
  challengeImage,
  challengeDescription,
  challengeDistanceGoal,
  selectedSportLength,
  challengeStartDate,
  challengeEndDate
) {
  if (challengeName === "") {
    showErrorMessage("Bitte gebe deiner Challenge einen Namen.");
    return false;
  }

  if (challengeImage != null) {
    //Checks if the file is an image and smaller than 10MB
    if (challengeImage.size > 10000000) {
      showErrorMessage("Das Bild darf nicht größer als 10Mb sein.");
      return false;
    } else if (/^image/.test(challengeImage.type) === false) {
      showErrorMessage("Es sind nur Bilder zum hochladen erlaubt.");
      return false;
    }
  }

  if (challengeDescription === "") {
    showErrorMessage("Bitte gebe deiner Challenge einen Beschreibung.");
    return false;
  }

  if (challengeDistanceGoal === 0) {
    showErrorMessage("Das Ziel für deine Challenge muss größer als 0 sein.");
    return false;
  }

  if (selectedSportLength === 0) {
    showErrorMessage("Du musst mindestens eine Sportart für deine Challenge auswählen.");
    return false;
  }

  if (challengeStartDate === "") {
    showErrorMessage("Bitte wähle ein start Datum für deine Challenge aus.");
    return false;
  }

  if (challengeEndDate === "") {
    showErrorMessage("Bitte wähle ein end Datum für deine Challenge aus.");
    return false;
  }

  return true;
}

/**
 * @param challengeObj Object of the challenge that should be saved
 * @param sportIDs all sportIDs which should be part of the challenge
 * @param sportFactors all sportFactors coressponding to the sportIDs
 * @param image The image file of the challenge null for standart image
 * @param imageID ID of the Image (only for update)
 * @param action add or edit
 * @returns If successfull data of the saved challenge otherwise nothing
 */
export async function saveOrUpdateBonus(challengeID, challengeObj, sportIDs, sportFactors, image, imageID, action) {
  let apiResponse, challengeResData, imageResData;
  let imageFetchBodyData = new FormData();

  if (image != null) {
    imageFetchBodyData.append("file", image);
    if (action === "add") {
      apiResponse = await apiFetch("/images/", "POST", {}, imageFetchBodyData);
    } else {
      apiResponse = await apiFetch("/images/" + imageID + "/", "PUT", {}, imageFetchBodyData);
    }

    if (apiResponse.error === false) {
      imageResData = apiResponse.resData;
    } else {
      showErrorMessage("Beim speichern der Challenge ist ein fehler aufgetreten! " + apiResponse.status);
      return;
    }
  } else {
    imageResData = { id: imageID };
  }

  challengeObj.imageID = imageResData.id;
  let query = "?imageId=" + imageResData.id;

  for (let i = 0; i < sportIDs.length; i++) {
    query += "&sportId=" + sportIDs[i];
    query += "&sportFactor=" + sportFactors[i];
  }

  apiResponse = await apiFetch(
    "/challenges/" + challengeID + "/" + query,
    "PUT",
    { Accept: "application/json", "Content-Type": "application/json" },
    JSON.stringify(challengeObj)
  );

  if (apiResponse.error === false) {
    challengeResData = apiResponse.resData;
    showSuccessMessage("Die Challenge wurde erfolgreich gespeichert.");
  } else {
    showErrorMessage("Beim speichern der Challenge ist ein fehler aufgetreten! " + apiResponse.status);
    return;
  }

  return challengeResData;
}
