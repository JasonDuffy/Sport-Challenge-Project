import { showErrorMessage } from "../../components/form/InfoMessage/InfoMessage";
import apiFetch from "../../utils/api";

/**
 * @author Robin Hackh
 */

export async function fetchActivityData(activityID){
    let apiResponse, activityResData, challengeSportResData, challengeResData, sportResData;

    apiResponse = await apiFetch("/activities/" + activityID + "/", "GET", {}, null);

    if (apiResponse.error === false) {
        activityResData = apiResponse.resData;
    } else {
      showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
      return;
    }

    apiResponse = await apiFetch("/challenge-sports/" + activityResData.challengeSportID + "/", "GET", {}, null);

    if (apiResponse.error === false) {
        challengeSportResData = apiResponse.resData;
    } else {
      showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
      return;
    }

    
    apiResponse = await apiFetch("/challenges/" + challengeSportResData.challengeID + "/", "GET", {}, null);

    if (apiResponse.error === false) {
        challengeResData = apiResponse.resData;
    } else {
      showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
      return;
    }

    apiResponse = await apiFetch("/sports/" + challengeSportResData.sportID + "/", "GET", {}, null);

    if (apiResponse.error === false) {
        sportResData = apiResponse.resData;
    } else {
      showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
      return;
    }

    return { activityResData, challengeSportResData, challengeResData, sportResData }
}

export async function fetchDeleteActivity(activityID){
    let apiResponse, deleteOk = false;

    apiResponse = await apiFetch("/activities/" + activityID + "/", "DELETE", {}, null);

    if (apiResponse.error === false) {
        deleteOk = true;
    }

    return deleteOk;
}

export async function fetchActivitySports(challengeID){
    let apiResponse, sportResData, challengeSportsResData;
    let sportsArray = [];

    apiResponse = await apiFetch("/challenges/" + challengeID + "/challenge-sports/", "GET", {}, null);

    if (apiResponse.error === false) {
        challengeSportsResData = apiResponse.resData;
    } else {
      showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
      return;
    }

    for (const challengeSport of challengeSportsResData) {
        apiResponse = await apiFetch("/sports/" + challengeSport.sportID + "/", "GET", {}, null);

        if (apiResponse.error === false) {
            sportResData = apiResponse.resData;
            sportResData.id = challengeSport.id;
            sportsArray.push(sportResData);
        } else {
          showErrorMessage("Beim laden der Seite ist ein Fehler aufgetreten!");
          return;
        }   
    }

    return sportsArray;
}

export async function updateActivity(activityID, activityObj){
    let apiResponse, updateOk = false;

    apiResponse = await apiFetch("/activities/" + activityID + "/", "PUT", {Accept: "application/json", "Content-Type": "application/json"}, JSON.stringify(activityObj));

    if (apiResponse.error === false) {
        updateOk = true;
    }

    return updateOk;
}