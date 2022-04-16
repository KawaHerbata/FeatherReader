import flask
from PIL import Image
from flask import Flask
from multipart import to_bytes as tob
from io import BytesIO
from datetime import datetime
import redis
import random

app = Flask(__name__)

@app.route("/", methods=['POST'])
def recognize_species():

    now = datetime.now()
    current_time = now.strftime("%d/%m/%y %H:%M:%S")

    data = flask.request.get_data()

    code = str(random.randint(1000, 9999))+current_time

    #image = Image.open(BytesIO(tob(data)))
    #image.save("/home/jk/Documents/test.jpg", "JPEG")

    r = redis.Redis()

    r.hset(code, "image", tob(data))
    r.hset(code, "time", current_time)
    r.hset(code, "species", "none")

    now = datetime.now()
    diff = 0
    response = "none"
    while diff < 60000 and response == "none":
        response = r.hget(code, "species").decode("utf-8")
        diff = (datetime.now() - now).total_seconds()*1000

    if response != "none":
        r.delete(code)
        return response
    else:
        r.delete(code)
        raise ConnectionError