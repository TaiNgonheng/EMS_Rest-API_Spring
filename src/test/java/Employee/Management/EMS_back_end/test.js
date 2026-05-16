import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
  stages: [
    { duration: '10s', target: 1 },   // baseline
    { duration: '20s', target: 50 },  // load test
    { duration: '20s', target: 100 }, // stress
    { duration: '20s', target: 200 }, // push limits
  ],
};

export default function () {
  http.get('http://localhost:8080/api/employees');
  sleep(1);
}