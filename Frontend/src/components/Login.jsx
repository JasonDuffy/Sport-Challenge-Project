import React, { Component } from "react";
import Button from "./Button";
import "./css/Login.css";

class Login extends Component {
  constructor() {
    super();
  }

  loginUser() {
    window.open("http://localhost:8081/saml/login/", "_self");
  }

  componentDidMount() {
    const pageLoading = document.getElementById("page_loading");
    pageLoading.parentNode.removeChild(pageLoading);
    document.getElementById("page").style.display = "block";
  }

  render() {
    return (
      <section className="background_white">
        <div className="section_container">
          <div className="section_content">
            <div className="center_content mg_t_10">
              <img className="login_logo" alt="scp_logo" src={require("../images/Challenge-Overlay.png")}></img>
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
