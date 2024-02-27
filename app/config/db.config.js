module.exports = {
  HOST: "192.168.192.27",
  USER: "postgres",
  PASSWORD: "password",
  DB: "testdb",
  dialect: "postgres",
  pool: {
    max: 5,
    min: 0,
    acquire: 30000,
    idle: 10000
  }
};