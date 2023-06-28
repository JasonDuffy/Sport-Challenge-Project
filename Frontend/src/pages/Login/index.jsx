import React, { Component } from "react";
import Button from "../../components/ui/button/Button";
import "./Login.css";
import GlobalVariables from "../../GlobalVariables.js"

class Login extends Component {
  constructor() {
    super();

    document.title = "Slash Challenge - Einloggen";
  }

  loginUser() {
    window.open(GlobalVariables.serverURL + "/saml/login/", "_self");
  }

  componentDidMount(){
    document.getElementById("page_loading").style.display = "none";
    document.getElementById("page").style.display = "block";
  }

  componentWillUnmount(){
    document.getElementById("page_loading").style.display = "flex";
    document.getElementById("page").style.display = "none";
  }

  render() {
    return (
      <section className="background_white">
        <div className="section_container">
          <div className="section_content">
            <div className="center_content mg_t_10">
              <img className="login_logo" alt="Logo der Slash Challenge" src={require("../../assets/images/Challenge-Overlay.png")}></img>
            </div>
            <div className="center_content mg_t_2">
              <Button color="orange" txt="Login" action={this.loginUser} />
            </div>
          </div>
        </div>
      </section>
    );
  }
}

export default Login;
