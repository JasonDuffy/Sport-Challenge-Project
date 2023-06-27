import React, { Component } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCircleDown, faGripVertical } from "@fortawesome/free-solid-svg-icons";
import "../../assets/css/form.css";
import "./AddTeam.css";
import Button from "../../components/ui/button/Button";
import withRouter from "../withRouter";
import GlobalVariables from "../../GlobalVariables.js"

class AddTeam extends Component {
  constructor(props) {
    super(props);

    document.title = "Slash Challenge - MISSING_DESC";

    this.state = {
      teamName: "",
      teamNameHeading: "",
      teamChallengId: 0,
      challengesData: [],
      allMembers: [],
      imageSource: "",
      imageID: 0,
      firstDrag: true,
      loading: false,
    };

    this.teamChallengIdChange = this.teamChallengIdChange.bind(this);
    this.teamNameChange = this.teamNameChange.bind(this);
    this.searchMember = this.searchMember.bind(this);
    this.previewImageChange = this.previewImageChange.bind(this);
    this.submitHandle = this.submitHandle.bind(this);
    this.dragHandler = this.dragHandler.bind(this);
    this.clearAllInputs = this.clearAllInputs.bind(this);
  }

  teamNameChange(event) {
    this.setState({ teamName: event.target.value });
  }

  teamChallengIdChange(event) {
    this.setState({ teamChallengId: event.target.value });
  }

  previewImageChange(event) {
    this.setState({ imageSource: URL.createObjectURL(event.target.files[0]) });
  }

  dragHandler() {
    let dragged = null;
    let listener = document.addEventListener;

    listener("dragstart", (event) => {
      dragged = event.target;
      return dragged;
    });

    listener("dragover", function (event) {
      return event.preventDefault();
    });

    listener("drop", (event) => {
      event.preventDefault();

      //If no member are in the Team and you drag one to it the overlay will be hidden
      if (this.state.firstDrag && event.target.id === "drag_target_overlay") {
        document.getElementById("drag_target_overlay").classList.remove("first_drag");
        this.setState({ firstDrag: false });
        dragged.parentNode.removeChild(dragged);
        event.target.parentNode.appendChild(dragged);
        return;
      }

      //Switch the members between the 2 boxes
      if (event.target.id === "member_in_team_dropzone" || event.target.id === "member_available_dropzone") {
        dragged.parentNode.removeChild(dragged);
        event.target.appendChild(dragged);
      } else if (event.target.className === "drag_member_drop" && event.target !== dragged) {
        const dropTarget = event.target.parentNode.parentNode;
        dragged.parentNode.removeChild(dragged);
        dropTarget.appendChild(dragged);
      }

      //If you dragged the last member out of the team the overlay will show again
      if (document.getElementById("member_in_team_dropzone").childNodes.length === 1) {
        document.getElementById("drag_target_overlay").classList.add("first_drag");
        this.setState({ firstDrag: true });
      }
    });
  }

  searchMember(event) {
    const memberAvailableEl = document.getElementById("member_available_dropzone");
    memberAvailableEl.innerHTML = "";

    for (const member of this.state.allMembers) {
      //if the search input matches the member display him
      if (member.fullName.toLowerCase().includes(event.target.value.toLowerCase())) {
        const alreadyInTeamMember = document.getElementById("member_in_team_dropzone").childNodes;
        let memberInTeam = false;

        //checks if the member is in the Team selection
        for (const inTeamMember of alreadyInTeamMember) {
          if (inTeamMember.childNodes[1].innerHTML.toLowerCase() === member.fullName.toLowerCase()) {
            memberInTeam = true; //if he is its set to true
            break;
          }
        }

        //only display users that are on the search site.
        if (memberInTeam === false) {
          const newDragMember = document.createElement("div");
          newDragMember.classList.add("drag_member");
          newDragMember.setAttribute("draggable", true);
          newDragMember.setAttribute("data-member-id", member.id);
          newDragMember.innerHTML =
            '<div class="drag_member_drop"></div><p>' +
            member.fullName +
            '</p><svg aria-hidden="true" focusable="false" data-prefix="fas" data-icon="grip-vertical" class="svg-inline--fa fa-grip-vertical " role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512"><path fill="currentColor" d="M40 352l48 0c22.1 0 40 17.9 40 40l0 48c0 22.1-17.9 40-40 40l-48 0c-22.1 0-40-17.9-40-40l0-48c0-22.1 17.9-40 40-40zm192 0l48 0c22.1 0 40 17.9 40 40l0 48c0 22.1-17.9 40-40 40l-48 0c-22.1 0-40-17.9-40-40l0-48c0-22.1 17.9-40 40-40zM40 320c-22.1 0-40-17.9-40-40l0-48c0-22.1 17.9-40 40-40l48 0c22.1 0 40 17.9 40 40l0 48c0 22.1-17.9 40-40 40l-48 0zM232 192l48 0c22.1 0 40 17.9 40 40l0 48c0 22.1-17.9 40-40 40l-48 0c-22.1 0-40-17.9-40-40l0-48c0-22.1 17.9-40 40-40zM40 160c-22.1 0-40-17.9-40-40L0 72C0 49.9 17.9 32 40 32l48 0c22.1 0 40 17.9 40 40l0 48c0 22.1-17.9 40-40 40l-48 0zM232 32l48 0c22.1 0 40 17.9 40 40l0 48c0 22.1-17.9 40-40 40l-48 0c-22.1 0-40-17.9-40-40l0-48c0-22.1 17.9-40 40-40z"></path></svg>';
          memberAvailableEl.appendChild(newDragMember);
        }
      }
    }
  }

  showInputErrorMessage(message) {
    const infoContainerEl = document.getElementById("form_info_container");
    const infoMessageEl = document.getElementById("form_info_message");
    infoContainerEl.classList.add("error");
    infoMessageEl.innerText = message;
    window.scrollTo(0, 0);
  }

  clearAllInputs() {
    document.getElementById("member_search_input").value = "";
    const teamImageEl = document.getElementById("team_image");
    teamImageEl.value = null;

    const memberInTeam = document.getElementById("member_in_team_dropzone");
    memberInTeam.innerHTML =
      '<div id="drag_target_overlay" class="first_drag"><svg aria-hidden="true" focusable="false" data-prefix="fas" data-icon="circle-down" class="svg-inline--fa fa-circle-down fa-3x " role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path fill="currentColor" d="M256 0a256 256 0 1 0 0 512A256 256 0 1 0 256 0zM376.9 294.6L269.8 394.5c-3.8 3.5-8.7 5.5-13.8 5.5s-10.1-2-13.8-5.5L135.1 294.6c-4.5-4.2-7.1-10.1-7.1-16.3c0-12.3 10-22.3 22.3-22.3l57.7 0 0-96c0-17.7 14.3-32 32-32l32 0c17.7 0 32 14.3 32 32l0 96 57.7 0c12.3 0 22.3 10 22.3 22.3c0 6.2-2.6 12.1-7.1 16.3z"></path></svg><p>Drop Teilnehmer hier</p></div>';

    const memberAvailableEl = document.getElementById("member_available_dropzone");
    memberAvailableEl.innerHTML = "";
    for (const member of this.state.allMembers) {
      const newDragMember = document.createElement("div");
      newDragMember.classList.add("drag_member");
      newDragMember.setAttribute("draggable", true);
      newDragMember.setAttribute("data-member-id", member.id);
      newDragMember.innerHTML =
        '<div class="drag_member_drop"></div><p>' +
        member.fullName +
        '</p><svg aria-hidden="true" focusable="false" data-prefix="fas" data-icon="grip-vertical" class="svg-inline--fa fa-grip-vertical " role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512"><path fill="currentColor" d="M40 352l48 0c22.1 0 40 17.9 40 40l0 48c0 22.1-17.9 40-40 40l-48 0c-22.1 0-40-17.9-40-40l0-48c0-22.1 17.9-40 40-40zm192 0l48 0c22.1 0 40 17.9 40 40l0 48c0 22.1-17.9 40-40 40l-48 0c-22.1 0-40-17.9-40-40l0-48c0-22.1 17.9-40 40-40zM40 320c-22.1 0-40-17.9-40-40l0-48c0-22.1 17.9-40 40-40l48 0c22.1 0 40 17.9 40 40l0 48c0 22.1-17.9 40-40 40l-48 0zM232 192l48 0c22.1 0 40 17.9 40 40l0 48c0 22.1-17.9 40-40 40l-48 0c-22.1 0-40-17.9-40-40l0-48c0-22.1 17.9-40 40-40zM40 160c-22.1 0-40-17.9-40-40L0 72C0 49.9 17.9 32 40 32l48 0c22.1 0 40 17.9 40 40l0 48c0 22.1-17.9 40-40 40l-48 0zM232 32l48 0c22.1 0 40 17.9 40 40l0 48c0 22.1-17.9 40-40 40l-48 0c-22.1 0-40-17.9-40-40l0-48c0-22.1 17.9-40 40-40z"></path></svg>';
      memberAvailableEl.appendChild(newDragMember);
    }

    this.setState({ teamName: "" });
    this.setState({ teamChallengId: 0 });
    this.setState({ imageSource: "" });
  }

  async submitHandle(event) {
    event.preventDefault();

    //Deactivate Button and add the loading circle
    this.setState({ loading: true });

    const infoContainerEl = document.getElementById("form_info_container");
    const teamImageEl = document.getElementById("team_image");
    const teamMembersEl = document.getElementById("member_in_team_dropzone");

    infoContainerEl.classList.remove("success");
    infoContainerEl.classList.remove("error");

    //Checks if the name is null (length is checked by HTML -> maxLength is 15)
    if (this.state.teamName === "") {
      this.showInputErrorMessage("Bitte gebe deinem Team einen Namen!");
      return;
    }

    //Checks if the user has selected a Challenge
    if (this.state.teamChallengId === 0) {
      this.showInputErrorMessage("Bitte wähle aus welcher Challenge dein Team angehören soll!");
      return;
    }

    //Does not check the image no image is uploaded
    if (teamImageEl.files[0] != null) {
      //Checks if the file is an image, not null and smaller than 10MB
      if (teamImageEl.files[0].size > 10000000) {
        this.showInputErrorMessage("Das Bild darf nicht größer als 10Mb sein!");
        return;
      } else if (teamImageEl.files[0].type.startsWith("image") === false) {
        this.showInputErrorMessage("Es sind nur Bilder zum hochladen erlaubt!");
        return;
      }
    }

    //Checks if min one Member is selected (there is always 1 child the Overlay so it must be min 2)
    if (teamMembersEl.childNodes.length < 2) {
      this.showInputErrorMessage("Du musst für dein Team mindestens einen Member auswählen!");
      return;
    }

    let teamJsonObj = {};
    teamJsonObj.name = this.state.teamName;
    teamJsonObj.challengeID = this.state.teamChallengId;

    if (this.props.params.action === "Add") {
      let fetchBodyData = new FormData();
      fetchBodyData.append("file", teamImageEl.files[0]);
      fetchBodyData.append("json", JSON.stringify(teamJsonObj));

      const teamResponse = await fetch(GlobalVariables.serverURL + "/teams/", { method: "POST", body: fetchBodyData, credentials: "include" });
      const teamResData = await teamResponse.json();

      if (!teamResponse.ok) {
        this.showInputErrorMessage("Beim erstellen des Teams ist etwas ist etwas schief gelaufen: " + teamResponse.status + " " + teamResponse.statusText + "!");
        return;
      }

      const teamMember = teamMembersEl.childNodes;
      for (let i = 1; i < teamMember.length; i++) {
        let teamMemberJsonObj = {};
        teamMemberJsonObj.memberID = teamMember[i].dataset.memberId;
        teamMemberJsonObj.teamID = teamResData.id;
        const teamMembersResponse = await fetch(GlobalVariables.serverURL + "/teamMembers/", {
          method: "POST",
          headers: { Accept: "application/json", "Content-Type": "application/json" }, //Needed: Backend will not accept without
          body: JSON.stringify(teamMemberJsonObj),
          credentials: "include",
        });

        if (!teamMembersResponse.ok) {
          this.showInputErrorMessage(
            "Beim erstellen des Teams ist etwas ist etwas schief gelaufen: " + teamMembersResponse.status + " " + teamMembersResponse.statusText + "!"
          );
          return;
        }
      }

      const infoMessageEl = document.getElementById("form_info_message");
      infoContainerEl.classList.add("success");
      infoMessageEl.innerText = "Das Team wurde erfolgreich erstellt! Wenn du möchtests kannst du ein weiteres Team erstellen.";
      window.scrollTo(0, 0);
      this.clearAllInputs();
    } else if (this.props.params.action === "Edit") {
      //If a new Image is set it will update the image corresponding to the team
      if (teamImageEl.files[0] != null) {
        //Creates body data for the update image fetch
        let fetchImageBodyData = new FormData();
        fetchImageBodyData.append("file", teamImageEl.files[0]);

        //Gives data to the Backend and updates the Image
        let imageResponse = await fetch(GlobalVariables.serverURL + "/images/" + this.state.imageID + "/", { method: "PUT", body: fetchImageBodyData, credentials: "include" });
        if (!imageResponse.ok) {
          this.showInputErrorMessage("Beim editieren der Challenge ist etwas schief gelaufen: " + imageResponse.status + " " + imageResponse.statusText + "!");
        }
      }

      const teamMemberResponse = await fetch(GlobalVariables.serverURL + "/members/teams/" + this.props.params.id + "/", { method: "GET", credentials: "include" });
      const teamMemberResData = await teamMemberResponse.json();

      for (const teamMember of teamMemberResData) {
        if (teamMembersEl.querySelector('[data-member-id="' + teamMember.userID + '"][class="drag_member"]') !== null) {
          teamMembersEl.removeChild(document.querySelector('[data-member-id="' + teamMember.userID + '"][class="drag_member"]'));
        } else {
          const teamMemberIdResponse = await fetch(GlobalVariables.serverURL + "/teamMembers/teams/" + this.props.params.id + "/members/" + teamMember.userID + "/", { method: "GET", credentials: "include" });
          const teamMemberIdResData = await teamMemberIdResponse.json();
          const removeTeamMemberResponse = await fetch(GlobalVariables.serverURL + "/teamMembers/" + teamMemberIdResData.id + "/", { method: "DELETE", credentials: "include" });
          if (!removeTeamMemberResponse.ok) {
            this.showInputErrorMessage(
              "Beim editieren des Teams ist etwas ist etwas schief gelaufen: " + removeTeamMemberResponse.status + " " + removeTeamMemberResponse.statusText + "!"
            );
            return;
          }
        }
      }

      let teamMemberJsonObj = {};
      teamMemberJsonObj.teamID = this.props.params.id;
      const teamMember = teamMembersEl.childNodes;

      for (let i = 1; i < teamMember.length; i++) {
        teamMemberJsonObj.memberID = teamMember[i].dataset.memberId;

        const teamMembersResponse = await fetch(GlobalVariables.serverURL + "/teamMembers/", {
          method: "POST",
          headers: { Accept: "application/json", "Content-Type": "application/json" }, //Needed: Backend will not accept without
          body: JSON.stringify(teamMemberJsonObj),
          credentials: "include",
        });

        if (!teamMembersResponse.ok) {
          this.showInputErrorMessage(
            "Beim editieren des Teams ist etwas ist etwas schief gelaufen: " + teamMembersResponse.status + " " + teamMembersResponse.statusText + "!"
          );
          return;
        }
      }

      const teamResponse = await fetch(GlobalVariables.serverURL + "/teams/" + this.props.params.id + "/?imageID=" + this.state.imageID, {
        method: "PUT",
        headers: { Accept: "application/json", "Content-Type": "application/json" },
        body: JSON.stringify(teamJsonObj),
        credentials: "include",
      });

      if (!teamResponse.ok) {
        this.showInputErrorMessage("Beim editieren des Teams ist etwas ist etwas schief gelaufen: " + teamResponse.status + " " + teamResponse.statusText + "!");
        return;
      }

      const infoMessageEl = document.getElementById("form_info_message");
      infoContainerEl.classList.add("success");
      infoMessageEl.innerText = "Die Änderungen wurden erfolgreich gespeichtert! Wenn du möchtests kannst du ein weiteres Team erstellen.";
      window.scrollTo(0, 0);
      this.clearAllInputs();
    }

    //Activates the again Button and removes the loading circle
    this.setState({ loading: false });
  }

  async componentDidMount() {
    this.dragHandler();

    const challengeResponse = await fetch(GlobalVariables.serverURL + "/challenges/?type=current", { method: "GET", credentials: "include" });
    const challengeResData = await challengeResponse.json();
    const memberResponse = await fetch(GlobalVariables.serverURL + "/members/", { method: "GET", credentials: "include" });
    const memberResData = await memberResponse.json();

    this.setState({ challengesData: challengeResData });

    for (const member of memberResData) {
      let memberObj = {};
      memberObj.fullName = member.firstName + " " + member.lastName;
      memberObj.id = member.userID;
      this.state.allMembers.push(memberObj);
    }

    if (this.props.params.action === "Edit") {
      const teamResponse = await fetch(GlobalVariables.serverURL + "/teams/" + this.props.params.id + "/", { method: "GET", credentials: "include" });
      const teamResData = await teamResponse.json();
      const imageResponse = await fetch(GlobalVariables.serverURL + "/images/" + teamResData.imageID + "/", { method: "GET", credentials: "include" });
      const imageResData = await imageResponse.json();
      const teamMemberResponse = await fetch(GlobalVariables.serverURL + "/members/teams/" + this.props.params.id + "/", { method: "GET", credentials: "include" });
      const teamMemberResData = await teamMemberResponse.json();

      this.setState({ teamNameHeading: teamResData.name });
      this.setState({ teamName: teamResData.name });
      this.setState({ imageID: teamResData.imageID });
      this.setState({ teamChallengId: teamResData.challengeID });
      this.setState({ imageSource: "data:" + imageResData.type + ";base64, " + imageResData.data });

      const memberInTeam = document.getElementById("member_in_team_dropzone");
      document.getElementById("drag_target_overlay").classList.remove("first_drag");
      this.setState({ firstDrag: false });

      for (const member of teamMemberResData) {
        const memberEl = document.querySelector('[data-member-id="' + member.userID + '"][class="drag_member"]');
        memberEl.parentNode.removeChild(memberEl);
        memberInTeam.appendChild(memberEl);
      }
    }

    const pageLoading = document.getElementById("page_loading");
    pageLoading.parentNode.removeChild(pageLoading);
    document.getElementById("page").style.display = "block";
  }

  render() {
    return (
      <section className="background_white">
        <div className="section_container">
          <div className="section_content">
            <div className="heading_underline_center mg_b_10">
              {this.props.params.action === "Edit" && <span className="underline_center">Team {this.state.teamNameHeading} editieren</span>}
              {this.props.params.action === "Add" && <span className="underline_center">Team hinzufügen</span>}
            </div>
            <div id="form_info_container" className="pd_1 mg_b_2">
              <span id="form_info_message"></span>
            </div>
            <div className="form_container">
              <form onSubmit={this.submitHandle}>
                <div className="form_input_container pd_1">
                  <h2>Gib deinem Team einen Namen</h2>
                  <input className="mg_t_2" type="text" placeholder="Team Name" maxLength={15} value={this.state.teamName} onChange={this.teamNameChange}></input>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Wähle eine Challenge aus, der das Team angehören soll</h2>
                  <select className="mg_t_1" value={this.state.teamChallengId} onChange={this.teamChallengIdChange}>
                    <option value={0} disabled>
                      Challenge wählen
                    </option>
                    {this.state.challengesData.map((item) => (
                      <option key={item.id} value={item.id}>
                        {item.name}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <div>
                    <h2>Wähle ein Bild für dein Team</h2>
                    <span className="form_input_description">
                      Das Bild repräsentiert dein Team in Challenges.
                      <br />
                      <br />
                      Das Bild sollte quadratisch sein.
                    </span>
                    <br />
                    <input id="team_image" className="mg_t_2" type="file" accept="image/*" onChange={this.previewImageChange}></input>
                  </div>
                  <div>
                    {this.state.imageSource !== "" && (
                      <div className="mg_t_2">
                        <img src={this.state.imageSource} alt="Aktuelles Bild der Challenge" height={200} width={200}></img>
                      </div>
                    )}
                  </div>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Wähle deine Teammitglieder aus</h2>
                  <div className="team_member_container mg_t_3">
                    <div className="team_member_content">
                      <input id="member_search_input" type="text" placeholder="Suche Teilnehmer" onChange={this.searchMember}></input>
                      <div id="member_available_dropzone" className="member_available mg_t_1">
                        {this.state.allMembers.map((item) => (
                          <div className="drag_member" draggable="true" key={item.id} data-member-id={item.id}>
                            <div className="drag_member_drop"></div>
                            <p>{item.fullName}</p>
                            <FontAwesomeIcon icon={faGripVertical} />
                          </div>
                        ))}
                      </div>
                    </div>
                    <div className="team_member_content mg_l_3">
                      <span className="form_input_description">Teilnehmer in deinem Team</span>
                      <div id="member_in_team_dropzone" className="member_in_team mg_t_1">
                        <div id="drag_target_overlay" className="first_drag">
                          <FontAwesomeIcon icon={faCircleDown} size="3x" />
                          <p>Drop Teilnehmer hier</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="center_content mg_t_2">
                  {this.props.params.action === "Edit" && <Button color="orange" txt="Änderungen Speichern" type="submit" loading={this.state.loading} />}
                  {this.props.params.action === "Add" && <Button color="orange" txt="Team hinzufügen" type="submit" loading={this.state.loading} />}
                </div>
              </form>
            </div>
          </div>
        </div>
      </section>
    );
  }
}

export default withRouter(AddTeam);
