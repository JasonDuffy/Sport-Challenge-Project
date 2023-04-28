import React from "react";
import Button from "./Button";
import { useState, useEffect } from "react";

const userAction = async () => {
    const res = await fetch("http://localhost:8081/saml/user/", { method:"GET", credentials: "include" });
    console.log(res);
    const data = await res.json();
    console.log(data);
}
const userAction2 = () => {
    window.open("http://localhost:8081/saml/login/", "_self")
}
const userAction3 = async () => {
    const res = await fetch("http://localhost:8081/member/0", { method: "GET", credentials: "include" });
    console.log(res);
    const data = await res.json();
    console.log(data);
}

function Login(){
    return <div className="mg_t_8">
        <Button color="orange" txt="samlAPI" action={userAction} />
        <Button color="orange" txt="restAPI" action={userAction3} />
        <Button color="orange" txt="Login" action={userAction2} />
    </div>
}

export default Login;