import nltk
#nltk.download("stopwords")
#--------#

from nltk.corpus import stopwords
from pymystem3 import Mystem
from string import punctuation
import io


#Create lemmatizer and stopwords list
mystem = Mystem()
russian_stopwords = stopwords.words("russian")

#Preprocess function
def preprocess_text(text):
    tokens = mystem.lemmatize(text.lower())
    tokens = [token for token in tokens if token not in russian_stopwords\
              and token != " " \
              and token.strip() not in punctuation]

    text = " ".join(tokens)

    return text


total = ""
lemmer = {}
index = {}
for r in range(0, 2):
    cur = ""
    with io.open("./files/выкачка "+str(r)+".txt", encoding='utf-8') as file:
        for line in file:
            cur =cur +  " " + line
    total = total + " " + cur
    for i in cur.split(" "):
        d = preprocess_text(i)
        value = lemmer.get(d)
        if d in lemmer.keys():
            lemmer[d].append(i)
        else:
            lemmer[d] = [i]
        if d in index.keys():
            if r not in index[d]:
                index[d].append(r)
        else:
            index[d] = [r]
print(lemmer)
print(index)

""