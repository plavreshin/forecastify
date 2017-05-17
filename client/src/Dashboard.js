import React, { Component } from 'react';
import {GridList, GridTile} from 'material-ui/GridList';
import FlatButton from 'material-ui/FlatButton';
import RaisedButton from 'material-ui/RaisedButton';
import Subheader from 'material-ui/Subheader';
import IconButton from 'material-ui/IconButton';
import StarBorder from 'material-ui/svg-icons/toggle/star-border';
import PropTypes from 'prop-types';

const styles = {
    root: {
        display: 'flex',
        flexWrap: 'wrap',
        justifyContent: 'space-around',
    },
    gridList: {
        width: 500,
        height: 450,
        overflowY: 'auto',
    },
    temperatureButton: {
        margin: 12,
        position: 'relative',
        top: 30,
        left: 25,
    },
    warningButton: {
        position: 'relative',
        top: 10,
    },
    titleStyle: {
         color: 'rgb(0, 188, 212)',
    }
};

const cityNames = ["Tallinn", "Helsinki", "Havana"]

const GridTileMeasured = ({ct, measuredTemperatures}) => (
    <div>
        <Subheader key={ct}>Measured in {ct}</Subheader>
        {measuredTemperatures !== undefined && measuredTemperatures.map((loc, idx) => (
                    <GridTile 
                        key={idx}
                        actionIcon={<IconButton><StarBorder color="rgb(0, 188, 212)" /></IconButton>}
                        titlePosition="top"
                        titleStyle={styles.titleStyle}
                        titleBackground="rgba(0, 0, 0, 0)"
                        title={loc.measuredAt}>
                            <RaisedButton label={loc.temperature + " C"} primary={true} style={styles.temperatureButton}/>
                            { loc.validated === "Exceeded" &&
                                <FlatButton label="Temperature exceeded!" secondary={true} style={styles.warningButton}/>
                            }
                    </GridTile>   
                    ))}
    </div>    
);

GridTileMeasured.propTypes = {
    measuredTemperatures: PropTypes.array,
    ct: PropTypes.string.isRequired
}

class Dashboard extends Component {
    constructor(props) {
        super(props);
        this.state = {
            measuredTemperatures: new Map()
        };
    }

    componentDidMount() {
         for (let ct of cityNames) {
            this.fetchData(ct)
        }
    }

    fetchData(cityName) {
        fetch(`http://localhost:9000/api/location/${cityName}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            }
        })
        .then((resp) => resp.json())
        .then((json) => {
            console.info(`Loaded for ${cityName} json ${JSON.stringify(json)}`)
            this.setState((prev, props) => ({
                measuredTemperatures: prev.measuredTemperatures.set(cityName, json)
            }));
        })
        .catch((err) => console.error(`Failed to request ${err}`));
    }

    render() {
        return (
            <div style={styles.root}> 
                <GridList style={styles.gridList}>
                <div className="subHeader">
                    {cityNames.map((ct, idx) => (
                        <GridTileMeasured key={idx} measuredTemperatures={this.state.measuredTemperatures.get(ct)} ct={ct}/>
                    ))}                    
                </div>    
                </GridList>  
            </div>    
        );  
    };
}
export default Dashboard;
