import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import TextInput from "../../components/form/TextInput/TextInput";
import NumberInput from "../../components/form/NumberInput/NumberInput";
import Button from "../../components/ui/button/Button";
import InfoMessage from "../../components/form/InfoMessage/InfoMessage";
import AddHeading from "../../components/AddHeading/AddHeading";
import { checkSportInput, fetchSportData, saveOrUpdateSport } from "./AddSport";

/**
 * @author Robin Hackh
 */

function AddSport() {
  const action = useParams().action.toLocaleLowerCase();
  const navigate = useNavigate();
  const location = useLocation();

  const [sportHeadingName, setSportHeadingName] = useState("");
  const [sportName, setSportName] = useState("");
  const [sportFactor, setSportFactor] = useState(0);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function load() {
      if (action === "edit" && location.state.id !== 0) {
        let sportResData = await fetchSportData(location.state.id);
        setSportName(sportResData.name);
        setSportFactor(sportResData.factor);
      }
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

  async function sumbitHandle(event) {
    event.preventDefault();
    setLoading(true);

    if (checkSportInput(sportName, sportFactor)) {
      let sportObj = {};
      sportObj.name = sportName;
      sportObj.factor = sportFactor;

      await saveOrUpdateSport(location.state.id, sportObj, action);
    }

    setLoading(false);
  }

  return (
    <section className="background_white">
      <div className="section_container">
        <div className="section_content">
          <AddHeading action={action} entity="Sport" name={sportHeadingName} />
          <InfoMessage />
          <div className="form_container">
            <form onSubmit={sumbitHandle}>
              <div className="form_input_container pd_1">
                <h2>Gib deiner Sportart einen Namen</h2>
                <TextInput className="mg_t_2" value={sportName} setValue={setSportName} maxLength={15} placeholder="Sport Name" />
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <h2>Lege einen Faktor für deine Sportart fest</h2>
                <span className="form_input_description">
                  Die mit deiner Sportart zurückgelegten Kilometer werden mit diesem Faktor multipliziert.
                  <br />
                  <br />
                  Dies ist der Standart Faktor, er kann beim erstellen einer Challenge spezifisch für diese angepasst werden.
                </span>
                <br />
                <NumberInput className="mg_t_2" value={sportFactor} setValue={setSportFactor} min={0.1} step={0.1} />
              </div>
              <div className="center_content mg_t_2">
                {action === "edit" && <Button color="orange" txt="Sport bearbeiten" type="submit" loading={loading} />}
                {action === "add" && <Button color="orange" txt="Sport hinzufügen" type="submit" loading={loading} />}
              </div>
            </form>
          </div>
        </div>
      </div>
    </section>
  );
}

export default AddSport;
