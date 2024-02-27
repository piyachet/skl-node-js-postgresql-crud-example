module.exports = {
  HOST: "skl-crud-db.crigquc2s6l4.ap-southeast-1.rds.amazonaws.com",
  USER: "postgres",
  PASSWORD: "password",
  DB: "postgres",
  dialect: "postgres",
  pool: {
    max: 5,
    min: 0,
    acquire: 30000,
    idle: 10000
  }
};
