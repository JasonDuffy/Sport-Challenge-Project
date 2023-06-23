/**
 * This module holds global settings
 * @author Jason Patrick Duffy
 */
const GlobalVariables = {
    serverProtocol: process.env.REACT_APP_SERVERPROTOCOL == null ? "http" : process.env.REACT_APP_SERVERPROTOCOL, // Set to used protocol with backend server
    serverPort: process.env.REACT_APP_SERVERPORT == null ? 8081 : process.env.REACT_APP_SERVERPORT, // Set to port number of backend server
    serverName: process.env.REACT_APP_SERVERNAME == null ? "localhost" : process.env.REACT_APP_SERVERNAME, // Set to hostname of backend server
}

// Outside of declaration as it would not be possible to access name, port and protocol otherwise
GlobalVariables.serverURL = GlobalVariables.serverProtocol + "://" + GlobalVariables.serverName + ":" + GlobalVariables.serverPort;

export default GlobalVariables;