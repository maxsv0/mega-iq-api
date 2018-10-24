package com.max.appengine.springboot.megaiq.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.max.appengine.springboot.megaiq.model.IQQuestionsSet;

public interface QuestionsSetRepository extends JpaRepository<IQQuestionsSet, Integer> {

}
