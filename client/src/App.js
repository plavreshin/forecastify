import React, { Component } from 'react';
import './App.css';
import injectTapEventPlugin from 'react-tap-event-plugin'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'
import AppBar from 'material-ui/AppBar';
import Dashboard from './Dashboard'
  
injectTapEventPlugin();

class App extends Component {

  render() {
    return (
      <MuiThemeProvider>
        <div className="body">
          <AppBar title="Forecastify"/>
          <Dashboard />
        </div> 
      </MuiThemeProvider>
    );
  }
}

export default App;
