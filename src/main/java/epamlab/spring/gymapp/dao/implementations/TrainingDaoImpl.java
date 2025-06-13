package epamlab.spring.gymapp.dao.implementations;

import epamlab.spring.gymapp.dao.base.BaseDao;
import epamlab.spring.gymapp.dao.interfaces.CreateDao;
import epamlab.spring.gymapp.dao.interfaces.ReadDao;
import epamlab.spring.gymapp.model.Training;

public class TrainingDaoImpl extends BaseDao<Training, Long> implements
        CreateDao<Training,Long>,
        ReadDao<Training, Long> {

}
