FROM python:3

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

WORKDIR /usr/src/app

USER nobody:nogroup

COPY ./integration_tests.py .

CMD [ "python", "./integration_tests.py", "-v", "-i", "configuration.json" ]
