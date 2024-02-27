module.exports = {
  HOST: "database-1.crigquc2s6l4.ap-southeast-1.rds.amazonaws.com",
  USER: "postgres",
  PASSWORD: "password",
  DB: "testdb",
  dialect: "postgres",
  dialectOptions: {
  ssl: {
    require: true, // This will help you. But you will see nwe error
     rejectUnauthorized: false // This line will fix new error
  }
},
  pool: {
    max: 5,
    min: 0,
    acquire: 30000,
    idle: 10000
  }
};