/**
 * @author Robin Hackh
 */

const serverUrlConfig = {
  serverProtocol: "http", // Set to used protocol with backend server
  serverPort: 8081, // Set to port number of backend server
  serverName: "localhost", // Set to hostname of backend server
};

async function apiFetch(url, method, headers, data) {
  const serverURL = serverUrlConfig.serverProtocol + "://" + serverUrlConfig.serverName + ":" + serverUrlConfig.serverPort;
  let error = false;
  let status;
  let response;
  let resData;

  response = await fetch(serverURL + url, { method: method, headers: headers, body: data, credentials: "include" });
  status = response.status;

  if (!response.ok) {
    error = true;
    return { error, status };
  }

  if(method !== "DELETE"){
    resData = await response.json();
  }

  return { error, status, resData };
}

export default apiFetch;
