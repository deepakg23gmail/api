import connexion
from connexion import RestyResolver
from connexion.exceptions import ProblemException
from flask_injector import FlaskInjector
from injector import Binder

import errorhandler
import orm
from services.provider.ItemsProvider import ItemsProvider

db_session = None


def configure(binder: Binder) -> Binder:
    binder.bind(
        ItemsProvider,
        ItemsProvider(db_session)
        # ItemsProvider([{"Name": "Test 1"}])
    )


"""Configure swagger yaml, db, dependency injection classes and error handlers"""
if __name__ == '__main__':
    app = connexion.FlaskApp(__name__, port=9090, specification_dir='api/swagger/')
    app.add_api('helloworld-api.yaml', resolver=RestyResolver('api'))
    db_session = orm.init_db('sqlite:///:memory:')
    FlaskInjector(app=app.app, modules=[configure])
    app.add_error_handler(ZeroDivisionError, errorhandler.render_unauthorized)
    app.add_error_handler(ProblemException, errorhandler.render_customexception)
    app.run()
    application = app.app
