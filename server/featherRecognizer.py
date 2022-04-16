from datetime import datetime
import redis
import numpy
from PIL import Image
#import cv2 as cv
import csv
import io
import tensorflow as tf


SPECIES_CSV = "data/species_list.csv"
MODEL_PATH = "data/weights_100_classes.hdf5"
IMG_WIDTH, IMG_HEIGHT = 240, 40


def FeatherRecognizer(image_data):
    now = datetime.now()
    #current_time = now.strftime("%H:%M:%S")

    img = tf.keras.preprocessing.image.img_to_array(image_data)
    #img = tf.io.read_file("/home/jk/Documents/test.jpg")
    #img = tf.image.decode_jpeg(img, channels=3)
    img = tf.image.convert_image_dtype(img, tf.uint8)

    if tf.shape(img)[1] > tf.shape(img)[0]:
        img = tf.image.rot90(img, k=1)
    img = tf.image.convert_image_dtype(img, tf.float32)
    img = tf.image.resize(img, [IMG_WIDTH, IMG_HEIGHT])
    img = tf.expand_dims(img, axis=0)

    prediction = siec.predict(img)
    print(prediction)
    index = numpy.argmax(prediction[0])
    print(index)
    print(classes[index][0])

    return classes[index][0]





siec = tf.keras.models.load_model(MODEL_PATH)

with open(SPECIES_CSV) as f:
    reader = csv.reader(f)
    classes = list(reader)

print(classes[0][0])

r = redis.Redis()

r.delete("app_try1")

kontynuuj = True


while kontynuuj:
    klucze = r.keys()

    kod = ""
    longest = 0
    now = datetime.now()

    for klucz in klucze:
        print(klucz.decode("utf-8"))
        try:
            time_klucz = r.hget(klucz, "time").decode("utf-8")
            datetime_obj = datetime.strptime(time_klucz, "%d/%m/%y %H:%M:%S")

            diff = (now - datetime_obj).total_seconds() * 1000

            if diff > longest and r.hget(klucz, "species").decode("utf-8") == "none":
                longest = diff
                kod = klucz.decode("utf-8")

            #elif r.hget(klucz, "species").decode("utf-8") != "none":
             #   r.delete(klucz)
        except AttributeError:
            print("cokolwiek")

    if kod != "":

        obraz_b = r.hget(kod, "image")
        image = Image.open(io.BytesIO(obraz_b))
        #opencvImage = cv.cvtColor(numpy.array(image), cv.COLOR_RGB2BGR)
        #cv.imwrite("/home/jk/Documents/testopencv.jpg", opencvImage)

        r.hset(kod, "species", FeatherRecognizer(image))











