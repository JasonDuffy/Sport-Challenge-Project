import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router";
import "./AddChallenge.css";
import "../../assets/css/form.css";
import AddHeading from "../../components/AddHeading/AddHeading";
import InfoMessage, { hideInfoMessage } from "../../components/form/InfoMessage/InfoMessage";
import Button from "../../components/ui/button/Button";
import Datepicker, { converDateToString } from "../../components/form/Datepicker/Datepicker";
import Checkbox from "../../components/form/Checkbox/Checkbox";
import TextInput from "../../components/form/TextInput/TextInput";
import TextareaInput from "../../components/form/TextareaInput/TextareaInput";
import NumberInput from "../../components/form/NumberInput/NumberInput";
import ImageSelecter from "../../components/form/ImageSelecter/ImageSelecter";
import { checkChallengeInput, fetchChallengeData, fetchDeleteChallenge, fetchImageData, fetchSportTable, saveOrUpdateChallenge } from "./AddChallenge";

/**
 * @author Robin Hackh
 */

function AddChallenge() {
  const action = useParams().action.toLocaleLowerCase();
  const navigate = useNavigate();
  const location = useLocation();

  const [challengeHeadingName, setChallengeHeadingName] = useState("");
  const [challengeName, setChallengeName] = useState("");
  const [challengeImage, setChallengeImage] = useState();
  const [challengeImageSource, setChallengeImageSource] = useState();
  const [challengeImageID, setChallengeImageID] = useState(0);
  const [challengeDescription, setChallengeDescription] = useState("");
  const [challengeDistanceGoal, setChallengeDistanceGoal] = useState(1);
  const [challengeStartDate, setChallengeStartDate] = useState(converDateToString(new Date()));
  const [challengeEndDate, setChallengeEndDate] = useState(converDateToString(new Date()));
  const [sportTableData, setSportTableData] = useState([]);
  const [loading, setLoading] = useState(false);

  //Load Component
  useEffect(() => {
    async function load() {
      let pageData;

      if (action === "edit") {
        pageData = await fetchChallengeData(location.state.id);

        setChallengeHeadingName(pageData.name);
        setChallengeName(pageData.name);
        setChallengeImageID(pageData.imageID);
        setChallengeDescription(pageData.description);
        setChallengeStartDate(pageData.startDate);
        setChallengeEndDate(pageData.endDate);
        setChallengeDistanceGoal(pageData.targetDistance);

        document.title = "Slash Challenge - " + pageData.name + " bearbeiten";

        //Use default image if no image was set
        if (pageData.imageID === null || pageData.imageID === 0) {
          setChallengeImageSource(require("../../assets/images/Default-Challenge.png"));
        } else {
          pageData = await fetchImageData(pageData.imageID);
          setChallengeImageSource("data:" + pageData.type + "; base64, " + pageData.data);
        }
      } else {
        document.title = "Slash Challenge - Neue Challenge erstellen";
      }

      pageData = await fetchSportTable(location.state.id);
      setSportTableData(pageData.sportDisplayArray);
    }

    load();
    document.getElementById("page_loading").style.display = "none";
    document.getElementById("page").style.display = "block";
  }, [action]);

  //Component unmount
  useEffect(() => {
    return () => {
      document.getElementById("page_loading").style.display = "flex";
      document.getElementById("page").style.display = "none";
    };
  }, []);

  async function submitHandle(event) {
    event.preventDefault();

    setLoading(true);
    hideInfoMessage();

    let sportIDs = [];
    let sportFactors = [];
    let challengeObj = {};

    // Get all sports that were checked and their factors
    const tableCells = document.getElementsByClassName("form_table_sport_checked");
    for (const tableCell of tableCells) {
      if (tableCell.getElementsByTagName("Input")[0].checked === true) {
        sportIDs.push(tableCell.dataset.sportIdChecked);
        sportFactors.push(document.querySelector('[data-sport-id-factor="' + tableCell.dataset.sportIdChecked + '"]').getElementsByTagName("Input")[0].value);
      }
    }

    let resData;

    // Check every challenge input for validity (i.e. challenge has a name, image smaller than 10mb, etc.)
    if (checkChallengeInput(challengeName, challengeImage, challengeDescription, challengeDistanceGoal, sportIDs.length, challengeStartDate, challengeEndDate)) {
      challengeObj.name = challengeName;
      challengeObj.description = challengeDescription;
      challengeObj.startDate = challengeStartDate;
      challengeObj.endDate = challengeEndDate;
      challengeObj.targetDistance = challengeDistanceGoal;

      resData = await saveOrUpdateChallenge(location.state.id, challengeObj, sportIDs, sportFactors, challengeImage, challengeImageID, action);
    }

    if (resData != null) {
      navigate("/challenge", { state: { challengeID: resData.id } });
    }

    setLoading(false);
  }

  async function deleteChallenge(event){
    event.preventDefault();

    if (window.confirm("Möchten Sie die Challenge " + challengeName + " wirklich löschen?")) {
      //if delete was successfully
      if (await fetchDeleteChallenge(location.state.id)) {
        navigate("/");
      }
    }
  }

  return (
    <section className="background_white">
      <div className="section_container">
        <div className="section_content">
          <AddHeading action={action} entity="Challenge" name={challengeHeadingName} />
          <InfoMessage />
          <div className="form_container">
            <form onSubmit={submitHandle}>
              <div className="form_input_container pd_1">
                <h2>Gebe der Challenge einen Namen</h2>
                <TextInput className="mg_t_2" value={challengeName} setValue={setChallengeName} maxLength={64} placeholder="Challenge Name" />
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <div>
                  <h2>Wähle ein Bild für deine Challenge (Optional)</h2>
                  <span className="form_input_description">
                    Das Bild repräsentiert deine Challenge auf der Startseite.
                    <br />
                    <br />
                    Es sollte quadratisch sein.
                  </span>
                  <br />
                  <ImageSelecter
                    className="mg_t_2"
                    value={challengeImage}
                    setValue={setChallengeImage}
                    alt="Aktuelles Bild der Challenge"
                    source={challengeImageSource}
                  />
                </div>
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <h2>Beschreibe deine Challenge</h2>
                <div className="form_input_description_content">
                  <TextareaInput
                    className="mg_t_2"
                    maxLength={400}
                    value={challengeDescription}
                    setValue={setChallengeDescription}
                    placeholder="Beschreibe deine Challenge"
                  />
                </div>
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <h2>Definiere ein Ziel für deine Challenge</h2>
                <span className="form_input_description">
                  Gebe hier die zu erreichende Punktzahl ein. Die Punktzahl besteht aus den geleisteten Kilometern, verrechnet mit dem jeweiligen Faktor der Sportart und
                  eventuellen Boni.
                </span>
                <NumberInput className="mg_t_2" value={challengeDistanceGoal} setValue={setChallengeDistanceGoal} min={1} step={1} />
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <h2>Wähle die Sportarten aus, die an der Challenge teilnehmen dürfen</h2>
                <table className="form_sport_table mg_t_2">
                  <thead>
                    <tr>
                      <th>Sportart</th>
                      <th>Faktor</th>
                      <th>Auswahl</th>
                    </tr>
                  </thead>
                  <tbody>
                    {sportTableData.map((item) => (
                      <tr key={item.id}>
                        <td>{item.name}</td>
                        <td data-sport-id-factor={item.id}>
                          <NumberInput className="form_table_sport_factor" value={item.factor} min={0.1} step={0.1} />
                        </td>
                        <td className="form_table_sport_checked" data-sport-id-checked={item.id}>
                          <Checkbox checked={item.checked} slider={true} />
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <h2>Wähle das Start- und Enddatum für deine Challenge</h2>
                <div className="form_input_date_container mg_t_2">
                  <Datepicker startDateValue={challengeStartDate} setStartDate={setChallengeStartDate} endDateValue={challengeEndDate} setEndDate={setChallengeEndDate} />
                </div>
              </div>
              <div className="center_content mg_t_2">
                {action === "edit" && (
                  <>
                    <Button color="orange" txt="Challenge bearbeiten" type="submit" loading={loading} />{" "}
                    <Button className="mg_l_2" action={deleteChallenge} color="red" txt="Challenge Löschen" loading={loading} />
                  </>
                )}
                {action === "add" && <Button color="orange" txt="Challenge hinzufügen" type="submit" loading={loading} />}
              </div>
            </form>
          </div>
        </div>
      </div>
    </section>
  );
}

export default AddChallenge;
