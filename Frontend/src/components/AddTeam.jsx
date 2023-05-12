import React, { Component } from "react";
import "./css/Form.css";
import "./css/AddTeam.css";

const allMembers = [
  "Erik Oesterle",
  "Heidrun Geckler",
  "Mila Lengemann",
  "Tabea Liesemeier",
  "Jasper Salmikeit",
  "Patrick Harder",
  "Anouk Lorenzen",
  "Mirco Gartner",
  "Suzanne Petersen",
];

class AddTeam extends Component {
  constructor() {
    super();
  }

  async submitHandle(event) {}

  dragHandler() {
    var dragged, listener;

    dragged = null;

    listener = document.addEventListener;

    listener("dragstart", (event) => {
      return (dragged = event.target);
    });

    listener("dragover", function (event) {
      return event.preventDefault();
    });

    listener("drop", (event) => {
      event.preventDefault();
      if (event.target.id === "member_in_team_dropzone" || event.target.id === "member_available_dropzone") {
        dragged.parentNode.removeChild(dragged);
        event.target.appendChild(dragged);
      } else if (event.target.className == "drag_member") {
        dragged.parentNode.removeChild(dragged);
        event.target.parentNode.appendChild(dragged);
      }
    });
  }

  searchMember(event) {
    const memberAvailableEl = document.getElementById("member_available_dropzone");
    memberAvailableEl.innerHTML = "";

    for (const member of allMembers) {
      if (member.includes(event.target.value)) {
        const newDragMember = document.createElement("div");
        newDragMember.classList.add("drag_member");
        newDragMember.setAttribute("draggable", true);
        newDragMember.innerHTML = member;
        memberAvailableEl.appendChild(newDragMember);
      }
    }
  }

  componentDidMount() {
    this.dragHandler();
  }

  render() {
    return (
      <section className="background_white">
        <div className="section_container">
          <div className="section_content">
            <div className="heading_underline_center mg_b_10">
              <span className="underline_center">Team hinzufügen</span>
            </div>
            <div className="form_container">
              <form onSubmit={this.submitHandle}>
                <div className="form_input_container pd_1">
                  <h2>Gib deinem Team einen Namen</h2>
                  <input className="mg_t_2" type="text" placeholder="Team Name"></input>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Wähle ein Bild für dein Team (Optional)</h2>
                  <span className="form_input_description">
                    Das Bild repräsentiert dein Team in Challenges.
                    <br />
                    <br />
                    Das Bild sollte quadratisch sein.
                  </span>
                  <br />
                  <input id="team_image" className="mg_t_2" type="file"></input>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Wähle deine Teammitglieder aus</h2>
                  <div className="team_member_container mg_t_3">
                    <div className="team_member_content">
                      <input
                        id="member_search_input"
                        type="text"
                        placeholder="Suche Teilnehmer"
                        onChange={this.searchMember}
                      ></input>
                      <div id="member_available_dropzone" className="member_available mg_t_1">
                        {allMembers.map((item) => (
                          <div className="drag_member" draggable="true" key={item}>
                            {item}
                          </div>
                        ))}
                      </div>
                    </div>
                    <div className="team_member_content mg_l_3">
                      <span className="form_input_description">Teilnehmer in deinem Team</span>
                      <div id="member_in_team_dropzone" className="member_in_team mg_t_1"></div>
                    </div>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
      </section>
    );
  }
}

export default AddTeam;
