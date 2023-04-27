import React from "react";
import Button from "./Button";
import { useState, useEffect } from "react";

const userAction = async () => {
    const res = await fetch("http://localhost:8081/saml/user");
    console.log(res);
    const data = await res.json();
    console.log(data);
}

function Login(){
    return <Button color="orange" txt="test" action={userAction}/>
}

export default Login;