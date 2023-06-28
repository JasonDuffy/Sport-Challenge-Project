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

    if (apiResponse.error === false) {
      challengeDropdownResData = apiResponse.resData;
    } else {
      showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
      return;
    }

    apiResponse = await apiFetch("/challenges/?type=future", "GET", {}, null);

    if (apiResponse.error === false) {
      challengeDropdownResData = challengeDropdownResData.concat(apiResponse.resData);
    } else {
      showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
      return;
    }
  } else {
    apiResponse = await apiFetch("/challenges/" + challengeID + "/", "GET", {}, null);

    if (apiResponse.error === false) {
      challengeDropdownResData = [apiResponse.resData];
    } else {
      showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
      return;
    }
  }

  return { challengeDropdownResData };
}

/**
 * @param challengeID ID of the Challenge
 * @returns a obj array with available members coresponding to the given ChallengeID
 */
export async function fetchAvailableMembers(challengeID) {
  if (Number(challengeID) === 0) return [];

  let apiResponse, memberResData;
  let memberDataArray = [];

  apiResponse = await apiFetch("/challenges/" + challengeID + "/members/?type=non", "GET", {}, null);

  if (apiResponse.error === false) {
    memberResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  for (const member of memberResData) {
    let memberObj = {
      fullName: member.firstName + " " + member.lastName,
      id: member.userID,
    };
    memberDataArray.push(memberObj);
  }

  return memberDataArray;
}

/**
 * @param teamID ID of the Team
 * @returns Data of the Team coresponding to the given teamID
 */
export async function fetchTeamData(teamID) {
  let apiResponse, teamResData, memberResData;
  let memberDataArray = [];

  apiResponse = await apiFetch("/teams/" + teamID + "/", "GET", {}, null);

  if (apiResponse.error === false) {
    teamResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  if (teamResData.imageID === null) teamResData.imageID = 0;

  apiResponse = await apiFetch("/teams/" + teamID + "/members/", "GET", {}, null);

  if (apiResponse.error === false) {
    memberResData = apiResponse.resData;
  } else {
    showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
    return;
  }

  for (const member of memberResData) {
    let memberObj = {
      fullName: member.firstName + " " + member.lastName,
      id: member.userID,
    };
    memberDataArray.push(memberObj);
  }

  return { teamResData, memberDataArray };
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
    showErrorMessage("Beim Laden der Seite ist ein Fehler aufgetreten!");
    return;
  }
  return imageResData;
}

export function checkTeamInput(teamName, challengeID, teamImage, teamMemberLength) {
  if (teamName === "") {
    showErrorMessage("Bitte gebe deinem Team einen Namen.");
    return false;
  }

  if (Number(challengeID) === 0) {
    showErrorMessage("Bitte wähle eine Challenge für dein Team aus.");
    return false;
  }

  if (teamImage != null) {
    //Checks if the file is an image and smaller than 10MB
    if (teamImage.size > 10000000) {
      showErrorMessage("Das Bild darf nicht größer als 10Mb sein.");
      return false;
    } else if (/^image/.test(teamImage.type) === false) {
      showErrorMessage("Es sind nur Bilder zum Hochladen erlaubt.");
      return false;
    }
  }

  if (teamMemberLength === 0) {
    showErrorMessage("Du musst mindestens ein Mitglied für dein Team auswählen.");
    return false;
  }

  return true;
}

export async function saveOrUpdateTeam(teamID, teamObj, memberData, image, imageID, action) {
  let apiResponse, teamResData, imageResData;
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
      showErrorMessage("Beim Speichern des Teams ist ein Fehler aufgetreten! " + apiResponse.status);
      return;
    }
  } else {
    imageResData = { id: imageID };
  }

  teamObj.imageID = imageResData.id;
  let query = "?imageID=" + imageResData.id;

  for (const member of memberData) {
    query += "&memberIDs=" + member.id;
  }

  apiResponse = await apiFetch("/teams/" + teamID + "/" + query, "PUT", { Accept: "application/json", "Content-Type": "application/json" }, JSON.stringify(teamObj));

  if (apiResponse.error === false) {
    teamResData = apiResponse.resData;
    showSuccessMessage("Das Team wurde erfolgreich gespeichert.");
  } else {
    showErrorMessage("Beim Speichern des Teams ist ein Fehler aufgetreten! " + apiResponse.status);
    return;
  }

  return teamResData;
}
