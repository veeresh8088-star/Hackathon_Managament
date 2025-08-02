import React, { Component } from "react";
import "../../App.css";
import axios from "axios";
import Navbar4 from "../Navbar/navbar5";
import cookie from "react-cookies";
import Footer from "../Footer/footer";
class mainpage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      properties: [{ Name: "Hackathon 1" }, { Name: "Hackathon 2" }],
      hackathons: [],
      authFlag: false,
      imageView: [],
      displayprop: "",
      leaderFlag: false,
      hackName: "",
      hackName2: "",
      payFlag: false
    };
    this.propertyChangeHandler = this.propertyChangeHandler.bind(this);
  }

  componentWillMount() {
    this.setState({
      authFlag: false
    });
  }
  propertyChangeHandler = e => {
    this.setState({
      displayprop: e.target.dataset.value
    });
    console.log("Successful test - ", this.state.displayprop);
  };
  componentDidMount() {
    const data = {
      owner: localStorage.getItem("screenName")
    };
    axios
      .get(`http://18.217.156.108:8080/hackathon/names/${data.owner}`)
      .then(response => {
        //update the state with the response data
        console.log(response);
        this.setState({
          authFlag: true,
          hackathons: response.data.body
        });
        console.log("Search :", this.state.properties);
        console.log("No of results :", this.state.properties.length);
      });
  }

  submitOpen(id) {
    const data = id;

    console.log(data);
    //set the with credentials to true
    axios.defaults.withCredentials = true;
    //make a post request with the user data
    axios
      .post(`http://18.217.156.108:8080/hackathon/${data}/opened`)
      .then(response => {
        console.log("Status Code : ", response);

        this.setState({
          authFlag: true,
          message: response.data.body
        });
        // window.location.reload(1);
      });
    //window.location.reload(1);
  }

  submitFinalize(id) {
    const data = id;

    console.log(data);
    //set the with credentials to true
    axios.defaults.withCredentials = true;
    //make a post request with the user data
    axios
      .post(`http://18.217.156.108:8080/hackathon/${data}/finalized`)
      .then(response => {
        console.log("Status Code : ", response);
        this.setState({
          authFlag: true,
          message: response.data.body
        });
        // window.location.reload(1);
      });
  }

  submitClose(id) {
    const data = id;

    console.log(data);
    //set the with credentials to true
    axios.defaults.withCredentials = true;
    //make a post request with the user data
    axios
      .post(`http://18.217.156.108:8080/hackathon/${data}/closed`)
      .then(response => {
        console.log("Status Code : ", response);
        this.setState({
          authFlag: true,
          message: response.data.body
        });
        // window.location.reload(1);
      });
  }

  submitLeaderboard = name => {
    this.setState({
      leaderFlag: true,
      hackName: name
    });
  };

  submitPaymentReport = name => {
    this.setState({
      payFlag: true,
      hackName2: name
    });
  };

  submitEarningReport = name => {
    this.setState({
      earningFlag: true,
      hackName3: name
    });
  };

  render() {
    let foot = <Footer data={this.props.data} />;
    console.log(this.props.location);
    let navbar = <Navbar4 data={this.props.data} />;

    let details = this.state.hackathons.map(hackathon => {
      // const imgurl1 = require(`../uploads/${property.img}`);

      if (this.state.leaderFlag) {
        this.props.history.push({
          pathname: "/resultPage",
          state: {
            hackName: this.state.hackName
          }
        });
      }

      if (this.state.payFlag) {
        this.props.history.push({
          pathname: "/paymentStatus",
          state: {
            hackName2: this.state.hackName2
          }
        });
      }

      if (this.state.earningFlag) {
        this.props.history.push({
          pathname: "/hackreport",
          state: {
            hackName3: this.state.hackName3
          }
        });
      }

      return (
        <div class="displaypropinfo container-fluid">
          {/* <div class="col-sm-4">
              <img src={imgurl1} height="200px" width="430px" />
            </div> */}

          <div class="col-sm-8">
            <div class="headline">
              <h3 class="hit-headline">
                <div
                // onClick={this.propertyChangeHandler}
                >
                  {hackathon.id}:{hackathon.name}
                </div>
              </h3>

              <button
                onClick={id => {
                  this.submitOpen(hackathon.id);
                }}
                style={{ marginLeft: "10px" }}
                class="btn btn-warning"
              >
                Open
              </button>
              <button
                onClick={id => {
                  this.submitClose(hackathon.id);
                }}
                style={{ marginLeft: "10px" }}
                class="btn btn-danger"
              >
                Close
              </button>
              <button
                onClick={id => {
                  this.submitFinalize(hackathon.id);
                }}
                style={{ marginLeft: "10px" }}
                class="btn btn-primary"
              >
                Finalize
              </button>
            </div>
          </div>
          <div class="col-sm-4">
            <button
              onClick={id => {
                this.submitLeaderboard(hackathon.name);
              }}
              style={{ float: "right", width: "180px" }}
              class="btn btn-primary"
            >
              View Leaderboard
            </button>
          </div>
          <div class="col-sm-4">
            <button
              onClick={id => {
                this.submitPaymentReport(hackathon.name);
              }}
              style={{ float: "right", width: "180px", marginTop: "5px" }}
              class="btn btn-warning"
            >
              Payment Report
            </button>
          </div>
          <div class="col-sm-4">
            <button
              onClick={id => {
                this.submitEarningReport(hackathon.name);
              }}
              style={{ float: "right", width: "180px", marginTop: "5px" }}
              class="btn btn-success"
            >
              Hackathon Earning Report
            </button>
          </div>
        </div>
      );
    });
    // let details = this.state.properties.map(property => {
    //   // const imgurl1 = require(`../uploads/${property.img}`);
    //   return (
    //     <div class="displaypropinfo container-fluid">
    //       {/* <div class="col-sm-4"><img src={imgurl1} height="200px" width="430px"></img></div> */}
    //       <div class="col-sm-8">
    //         <div class="headline">
    //           <h3 class="hit-headline">
    //             <a>
    //               <div
    //                 onClick={this.propertyChangeHandler}
    //                 name="displayprop"
    //                 data-value={property.Name}
    //               >
    //                 {property.Name}
    //               </div>
    //             </a>
    //           </h3>
    //         </div>
    //         <div class="propdetails">
    //           {property.Type} | <strong>{property.bednumber}</strong> BA |
    //           Sleeps <strong>{property.guests}</strong>
    //         </div>
    //         <div class="price-hit">
    //           <div class="subprice-hit">
    //             <button
    //               onClick={this.submitJoin}
    //               style={{
    //                 backgroundColor: "#0067db",
    //                 borderColor: "#0067db",
    //                 fontSize: "18px"
    //               }}
    //               class="btn btn-primary button-search"
    //             >
    //               Register
    //             </button>
    //           </div>
    //         </div>
    //       </div>
    //     </div>
    //   );
    // });
    let redirectVar = null;
    // if (this.state.displayprop != "") {
    //   this.props.history.push({
    //     pathname: "/property",
    //     state: {
    //       displayprop: this.state.displayprop
    //     }
    //   });
    // }

    if (this.state.properties !== "") {
      return (
        <div>
          {redirectVar}

          <div class="main-div1" style={{ backgroundColor: "#F7F7F8" }}>
            {navbar}
            <div>
              <h2 style={{ marginLeft: "38%" }}>Manage Your Hackathons</h2>
              <h4 style={{ marginLeft: "33%" }}>
                You can open, close and finalize hackathons you created here
              </h4>
            </div>
            <div
              style={{
                marginTop: "30px",
                marginLeft: "40px",
                fontSize: "15px",
                width: "200px",
                backgroundColor: "blue",
                color: "white"
              }}
            >
              {this.state.message}
            </div>

            {/*Display the Tbale row based on data recieved*/}
            {details}
          </div>
          {foot}
        </div>
      );
    } else {
      return (
        <div>
          <div class="main-div1">
            <h2>No results for this query</h2>
          </div>
        </div>
      );
    }
  }
}
export default mainpage;
