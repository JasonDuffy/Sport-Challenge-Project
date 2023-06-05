import { Component, React } from "react";
import "./css/MyChallenges.css";
import SportsTableRow from "./SportsTableRow";
import Button from "./Button";

/**
 * Page providing an overview of all available sports and allowing edit and add of sports
 * @author Jason Patrick Duffy
 */
class Sports extends Component {
  constructor(props) {
    super(props);

    this.state = {
      sports: []
    };
  }

  async componentDidMount() {
    let sportsResponse = await fetch("http://localhost:8081/sports/", { method: "GET", credentials: "include" });
    let sportsResData = await sportsResponse.json();

    this.setState({ sports: sportsResData })

    const pageLoading = document.getElementById("page_loading");
    pageLoading.parentNode.removeChild(pageLoading);
    document.getElementById("page").style.display = "block";
  }

  render() {
    return (
      <>
        <section className="background_lightblue">
          <div className="section_container">
            <div className="section_content">
              <div className="heading_underline_center mg_b_8">
                <span className="underline_center">Sportarten</span>
              </div>
              <div>
                <table className="last_activites_table">
                  <thead>
                    <tr>
                      <th>Sport</th>
                      <th>Faktor</th>
                      <th>Aktion</th>
                    </tr>
                  </thead>
                  <tbody>
                    {this.state.sports.map((item) => (
                      <SportsTableRow key={item.id} id={item.id}/>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </section>
        <section>
          <div className="center_content mg_t_2">
            <a href={'Add/Sport/0'} style={{ color: "#ffeeee" }}>
              <Button color="orange" txt="Neue Sportart" />
            </a>
          </div>
        </section>
      </>
    );
  }
}

export default Sports;
