// const express = require("express");
// const cors = require("cors");

// const app = express();

// var corsOptions = {
//   origin: "http://localhost:8081"
// };

// app.use(cors(corsOptions));

// // parse requests of content-type - application/json
// app.use(express.json());

// // parse requests of content-type - application/x-www-form-urlencoded
// app.use(express.urlencoded({ extended: true }));

// const db = require("./app/models");
// db.sequelize.sync()
//   .then(() => {
//     console.log("Synced db.");
//   })
//   .catch((err) => {
//     console.log("Failed to sync db: " + err.message + err.stack);
//   });

// // // drop the table if it already exists
// // db.sequelize.sync({ force: true }).then(() => {
// //   console.log("Drop and re-sync db.");
// // });

// // simple route
// app.get("/", (req, res) => {
//   res.json({ message: "Welcome to bezkoder application." });
// });

// require("./app/routes/turorial.routes")(app);

// // set port, listen for requests
// const PORT = process.env.PORT || 8080;
// app.listen(PORT, () => {
//   console.log(`Server is running on port ${PORT}.`);
// });


const express = require("express");
const cors = require("cors");

const app = express();

var corsOptions = {
  origin: "http://localhost:8081"
};

app.use(cors(corsOptions));

// parse requests of content-type - application/json
app.use(express.json());

// parse requests of content-type - application/x-www-form-urlencoded
app.use(express.urlencoded({ extended: true }));


// const sequelize = new Sequelize("testdb", "postgres", "password", {
//   host: "skl-crud-nodejs.crigquc2s6l4.ap-southeast-1.rds.amazonaws.com",
//   dialect: "postgres",

// });
// sequelize.authenticate().then(() => {
//   console.log('Connection has been established successfully.');
// }).catch((error) => {
//   console.error('Unable to connect to the database: ', error);
// });
// sequelize.sync()
//   .then(() => {
//     console.log("Synced db.");
//   })
//   .catch((err) => {
//     console.log("Failed to sync db: " + err.message + err.stack);
//   });

// // drop the table if it already exists
// db.sequelize.sync({ force: true }).then(() => {
//   console.log("Drop and re-sync db.");
// });

// simple route
app.get("/test", async (req, res) => {

  const { Client } = require('pg');

  const dbConfig = {
    host: 'skl-crud-nodejs.crigquc2s6l4.ap-southeast-1.rds.amazonaws.com',
    port: 5432,
    database: 'testdb',
    user: 'postgres',
    password: 'password',
  }
  const client = new Client(dbConfig);
  
  client.connect()
    .then(() => {
      console.log('Connected to PostgreSQL database');
    })
    .catch((err) => {
      console.error('Error connecting to PostgreSQL database', err);
    })
  client.query('SELECT * FROM customers', (err, result) => {
    if (err) {
      console.error('Error executing query', err);
    } else {
      console.log('Query result:', result.rows);
    }
  });
  res.json({ message: "Welcome to bezkoder application." });
});

// require("./app/routes/turorial.routes")(app);

// set port, listen for requests
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}.`);
});