import { Component, React } from "react";
import MyChallengeOverview from "./MyChallengeOverview";
import "./css/MyChallenges.css";
import MyChallengesTableRow from "./MyChallengesTableRow";

class MyChallenges extends Component {
  constructor(props) {
    super(props);

    this.state = {
      challengeIDs: [],
      activityIDs: [],
      loggedInID: 0,
    };
  }

  async componentDidMount() {
    let loggedInMemberResponse = await fetch("http://localhost:8081/members/loggedIn/", { method: "GET", credentials: "include" });
    let loggedInMemberResData = await loggedInMemberResponse.json();
    //EINFÜGEN FÜR DYNAMISCHE GENERIERUNG -> AKTUELL NICH MÖGLICH, DA MAX MUSTERMANN KEINER CHALLENGE ANGEHÖRT
    //let challengeIDsResponse = await fetch("http://localhost:8081/challenges/members/" + loggedInMemberResData.userID + "/", { method: "GET", credentials: "include" });
    let challengeIDsResponse = await fetch("http://localhost:8081/challenges/members/3/", { method: "GET", credentials: "include" });
    let challengeIDsResData = await challengeIDsResponse.json();

    let activitiesResponse = await fetch("http://localhost:8081/members/" + loggedInMemberResData.userID + "/activities/", { method: "GET", credentials: "include" });
    let activitiesResData = await activitiesResponse.json();

    this.setState({ loggedInID: loggedInMemberResData.userID });
    this.setState({ activityIDs: activitiesResData })
    this.setState({ challengeIDs: challengeIDsResData });

    const pageLoading = document.getElementById("page_loading");
    pageLoading.parentNode.removeChild(pageLoading);
    document.getElementById("page").style.display = "block";
  }

  render() {
    return (
      <>
        <section className="background_white">
          <div className="section_container">
            <div className="section_content">
              <div className="heading_underline_center mg_b_8">
                <span className="underline_center">Meine Challenges</span>
              </div>
              <ul className="col my_challenge_list">
                {this.state.challengeIDs.map((item) => (
                  <li key={item} className="my_challenge_list_item">
                    <MyChallengeOverview id={item} memberId={this.state.loggedInID} />
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </section>
        <section className="background_lightblue">
          <div className="section_container">
            <div className="section_content">
              <div className="heading_underline_center mg_b_8">
                <span className="underline_center">Meine Aktivitäten</span>
              </div>
              <div>
                <table className="last_activites_table">
                  <thead>
                    <tr>
                      <th>Challenge Name</th>
                      <th>Sportart</th>
                      <th>Distanz</th>
                      <th>Eingetragen am</th>
                      <th>Aktion</th>
                    </tr>
                  </thead>
                  <tbody>
                    {this.state.activityIDs.map((item) => (
                      <MyChallengesTableRow key={item.id} id={item.id}/>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </section>
      </>
    );
  }
}

export default MyChallenges;
