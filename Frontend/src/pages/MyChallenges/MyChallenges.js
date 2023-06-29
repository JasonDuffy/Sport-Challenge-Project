import { showErrorMessage } from "../../components/form/InfoMessage/InfoMessage";
import apiFetch from "../../utils/api";

/**
 * @author Robin Hackh
 */

export async function fetchLoggedInMember() {
  let apiResponse, memberResData;

  apiResponse = await apiFetch("/members/loggedIn/", "GET", {}, null);

  if (apiResponse.error === false) {
    memberResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  return memberResData;
}

export async function fetchActivityIDs(userID) {
  let apiResponse, activitiesResData;
  let activityIDs = [];

  apiResponse = await apiFetch("/members/" + userID + "/activities/", "GET", {}, null);

  if (apiResponse.error === false) {
    activitiesResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  for (const activityResData of activitiesResData) {
    activityIDs.push(activityResData.id);
  }

  return activityIDs;
}

export async function fetchChallengeIDs(userID) {
    let apiResponse, challengesResData;
    let challengeIDs = [];
  
    apiResponse = await apiFetch("/members/" + userID + "/challenges/current/", "GET", {}, null);
  
    if (apiResponse.error === false) {
        challengesResData = apiResponse.resData;
    } else {
      showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
      return;
    }
  
    for (const challengeResData of challengesResData) {
        challengeIDs.push(challengeResData.id);
    }
  
    return challengeIDs;
  }
  
