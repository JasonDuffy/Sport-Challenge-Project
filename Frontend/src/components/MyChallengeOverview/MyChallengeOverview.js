import { showErrorMessage } from "../../components/form/InfoMessage/InfoMessage";
import apiFetch from "../../utils/api";

/**
 * @author Robin Hackh
 */

export async function fetchChallengeData(challengeID) {
  let apiResponse, challengeResData, imageResData, distanceResData, challengeSportResData, sportResData;

  apiResponse = await apiFetch("/challenges/" + challengeID + "/", "GET", {}, null);

  if (apiResponse.error === false) {
    challengeResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  if (challengeResData.imageID != 0 && challengeResData.imageID != null){
    apiResponse = await apiFetch("/images/" + challengeResData.imageID + "/", "GET", {}, null);

    if (apiResponse.error === false) {
      imageResData = apiResponse.resData;
    } else {
      showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
      return;
    }
  }

  apiResponse = await apiFetch("/challenges/" + challengeID + "/distance/", "GET", {}, null);

  if (apiResponse.error === false) {
    distanceResData = apiResponse.resData;
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

  for (const challengeSport of challengeSportResData) {
    apiResponse = await apiFetch("/sports/" + challengeSport.sportID + "/", "GET", {}, null);

    if (apiResponse.error === false) {
      sportResData = apiResponse.resData;
      challengeSport.name = sportResData.name;
    } else {
      showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
      return;
    }
  }

  return { challengeResData, imageResData, distanceResData, challengeSportResData };
}

export async function saveActivity(activityObj) {
    let apiResponse, activityResData;

    apiResponse = await apiFetch("/activities/", "POST", {Accept: "application/json", "Content-Type": "application/json"}, JSON.stringify(activityObj));

    if (apiResponse.error === false) {
        activityResData = apiResponse.resData;
    } else {
        return null;
    }

    return activityResData;
}
