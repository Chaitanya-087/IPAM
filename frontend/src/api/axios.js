import axios from "axios";

const BASEURL = "http://localhost:8080";

const instance = axios.create({
  baseURL: BASEURL,
});
export const axiosPrivate = axios.create({
  baseURL: BASEURL,
  headers: {'Content-Type': 'application/json'},
})

export default instance;
