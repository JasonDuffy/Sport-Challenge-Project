import { Component, React } from "react";
import SportsTableRow from "../../components/SportsTableRow/SportsTableRow";
import Button from "../../components/ui/button/Button";
import GlobalVariables from "../../GlobalVariables.js";
import "../../assets/css/form.css";
import { Link } from "react-router-dom";

/**
 * Page providing an overview of all available sports and allowing edit and add of sports
 * @author Jason Patrick Duffy
 */
class Sports extends Component {
  constructor(props) {
    super(props);

    document.title = "Slash Challenge - Sportarten";

    this.state = {
      sports: [],
    };
  }

  async componentDidMount() {
    let sportsResponse = await fetch(GlobalVariables.serverURL + "/sports/", { method: "GET", credentials: "include" });
    let sportsResData = await sportsResponse.json();

    this.setState({ sports: sportsResData });

    document.getElementById("page_loading").style.display = "none";
    document.getElementById("page").style.display = "block";
  }

  componentWillUnmount() {
    document.getElementById("page_loading").style.display = "flex";
    document.getElementById("page").style.display = "none";
  }

  render() {
    return (
      <>
        <section className="background_white">
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
                      <SportsTableRow key={item.id} id={item.id} />
                    ))}
                  </tbody>
                </table>
              </div>
              <div className="center_content mg_t_2">
                <Link to="/sport/add" state={{ id: 0 }}>
                  <Button color="orange" txt="Neue Sportart" />
                </Link>
              </div>
            </div>
          </div>
        </section>
      </>
    );
  }
}

export default Sports;
