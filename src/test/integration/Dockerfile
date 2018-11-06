FROM python:3

COPY src/test/integration/requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

WORKDIR /usr/src/app

USER nobody:nogroup

COPY src/test/integration/integration_tests.py .
COPY swagger.yaml .

CMD [ "python", "./integration_tests.py", "-v", "-i", "configuration.json", "-s", "swagger.yaml"]
