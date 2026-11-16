-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 16, 2026 at 02:36 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `masters`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`username`, `password`, `email`) VALUES
('OSHAN', 'admin', 'odisanayaka@gmail.com');

-- --------------------------------------------------------

--
-- Table structure for table `al_salary`
--

CREATE TABLE `al_salary` (
  `subject_id` varchar(45) NOT NULL,
  `lecturer` varchar(45) NOT NULL,
  `salary` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `al_subject_prices`
--

CREATE TABLE `al_subject_prices` (
  `subject` varchar(45) NOT NULL,
  `price` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `attendance`
--

CREATE TABLE `attendance` (
  `admission_no` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `c_email` varchar(45) NOT NULL,
  `subject` varchar(45) NOT NULL,
  `grade` varchar(45) NOT NULL,
  `date` varchar(45) NOT NULL,
  `status` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `exam_papers`
--

CREATE TABLE `exam_papers` (
  `id` varchar(45) NOT NULL,
  `category` varchar(45) NOT NULL,
  `subject` varchar(45) NOT NULL,
  `paper` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lecturers`
--

CREATE TABLE `lecturers` (
  `lecturer_id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `contact` varchar(45) NOT NULL,
  `address` varchar(100) NOT NULL,
  `email` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lecturer_account`
--

CREATE TABLE `lecturer_account` (
  `username` int(11) NOT NULL,
  `password` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lecture_records`
--

CREATE TABLE `lecture_records` (
  `id` varchar(45) NOT NULL,
  `category` varchar(45) NOT NULL,
  `subject` varchar(45) NOT NULL,
  `record` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `menu`
--

CREATE TABLE `menu` (
  `id` int(11) NOT NULL,
  `status` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `menu`
--

INSERT INTO `menu` (`id`, `status`) VALUES
(1, 'ADMIN');

-- --------------------------------------------------------

--
-- Table structure for table `ol_salary`
--

CREATE TABLE `ol_salary` (
  `subject_id` varchar(45) NOT NULL,
  `lecturer` varchar(45) NOT NULL,
  `salary` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ol_subject_prices`
--

CREATE TABLE `ol_subject_prices` (
  `subject` varchar(45) NOT NULL,
  `price` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `salary`
--

CREATE TABLE `salary` (
  `subject_id` varchar(15) NOT NULL,
  `lecturer` varchar(45) NOT NULL,
  `salary` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `salary_paid`
--

CREATE TABLE `salary_paid` (
  `lecturer` varchar(45) NOT NULL,
  `category` varchar(45) NOT NULL,
  `salary_amount` double NOT NULL,
  `month` varchar(45) NOT NULL,
  `institute_cost` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `admission_no` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `birthday` varchar(45) NOT NULL,
  `gender` varchar(45) NOT NULL,
  `grade` varchar(45) NOT NULL,
  `school` varchar(45) NOT NULL,
  `address` varchar(100) NOT NULL,
  `custodian` varchar(45) NOT NULL,
  `c_contact` int(11) NOT NULL,
  `c_email` varchar(45) NOT NULL,
  `admission_date` varchar(45) NOT NULL,
  `leaving` varchar(45) NOT NULL,
  `e_contact` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `students_payments`
--

CREATE TABLE `students_payments` (
  `admission_no` int(11) NOT NULL,
  `month` varchar(45) NOT NULL,
  `status` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student_account`
--

CREATE TABLE `student_account` (
  `username` int(11) NOT NULL,
  `password` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `subjects_primary`
--

CREATE TABLE `subjects_primary` (
  `lecturer_id` int(11) NOT NULL,
  `sinhala` varchar(45) NOT NULL,
  `english` varchar(45) NOT NULL,
  `science` varchar(45) NOT NULL,
  `maths` varchar(45) NOT NULL,
  `history` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `subject_al`
--

CREATE TABLE `subject_al` (
  `lecturer_id` int(11) NOT NULL,
  `accounting` varchar(45) NOT NULL,
  `business_studies` varchar(45) NOT NULL,
  `econ` varchar(45) NOT NULL,
  `IT` varchar(45) NOT NULL,
  `english` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `subject_count_prices`
--

CREATE TABLE `subject_count_prices` (
  `count` int(11) NOT NULL,
  `price` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `subject_ol`
--

CREATE TABLE `subject_ol` (
  `lecturer_id` int(11) NOT NULL,
  `sinhala` varchar(45) NOT NULL,
  `english` varchar(45) NOT NULL,
  `science` varchar(45) NOT NULL,
  `maths` varchar(45) NOT NULL,
  `history` varchar(45) NOT NULL,
  `commerce` varchar(45) NOT NULL,
  `ICT` varchar(45) NOT NULL,
  `english_LIT` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `subject_table_one`
--

CREATE TABLE `subject_table_one` (
  `admission_no` int(11) NOT NULL,
  `sinhala` varchar(45) NOT NULL,
  `english` varchar(45) NOT NULL,
  `science` varchar(45) NOT NULL,
  `maths` varchar(45) NOT NULL,
  `history` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `subject_table_three`
--

CREATE TABLE `subject_table_three` (
  `admission_no` int(11) NOT NULL,
  `accounting` varchar(45) NOT NULL,
  `business_studies` varchar(45) NOT NULL,
  `econ` varchar(45) NOT NULL,
  `IT` varchar(45) NOT NULL,
  `english` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `subject_table_two`
--

CREATE TABLE `subject_table_two` (
  `admission_no` int(11) NOT NULL,
  `sinhala` varchar(45) NOT NULL,
  `english` varchar(45) NOT NULL,
  `science` varchar(45) NOT NULL,
  `maths` varchar(45) NOT NULL,
  `history` varchar(45) NOT NULL,
  `commerce` varchar(45) NOT NULL,
  `ICT` varchar(45) NOT NULL,
  `english_LIT` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`username`);

--
-- Indexes for table `al_salary`
--
ALTER TABLE `al_salary`
  ADD PRIMARY KEY (`subject_id`);

--
-- Indexes for table `al_subject_prices`
--
ALTER TABLE `al_subject_prices`
  ADD PRIMARY KEY (`subject`);

--
-- Indexes for table `attendance`
--
ALTER TABLE `attendance`
  ADD PRIMARY KEY (`admission_no`,`subject`,`date`);

--
-- Indexes for table `exam_papers`
--
ALTER TABLE `exam_papers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `lecturers`
--
ALTER TABLE `lecturers`
  ADD PRIMARY KEY (`lecturer_id`);

--
-- Indexes for table `lecturer_account`
--
ALTER TABLE `lecturer_account`
  ADD PRIMARY KEY (`username`);

--
-- Indexes for table `lecture_records`
--
ALTER TABLE `lecture_records`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `menu`
--
ALTER TABLE `menu`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `ol_salary`
--
ALTER TABLE `ol_salary`
  ADD PRIMARY KEY (`subject_id`);

--
-- Indexes for table `ol_subject_prices`
--
ALTER TABLE `ol_subject_prices`
  ADD PRIMARY KEY (`subject`);

--
-- Indexes for table `salary`
--
ALTER TABLE `salary`
  ADD PRIMARY KEY (`subject_id`);

--
-- Indexes for table `salary_paid`
--
ALTER TABLE `salary_paid`
  ADD PRIMARY KEY (`lecturer`,`month`,`category`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`admission_no`);

--
-- Indexes for table `students_payments`
--
ALTER TABLE `students_payments`
  ADD PRIMARY KEY (`admission_no`,`month`);

--
-- Indexes for table `student_account`
--
ALTER TABLE `student_account`
  ADD PRIMARY KEY (`username`);

--
-- Indexes for table `subjects_primary`
--
ALTER TABLE `subjects_primary`
  ADD PRIMARY KEY (`lecturer_id`);

--
-- Indexes for table `subject_al`
--
ALTER TABLE `subject_al`
  ADD PRIMARY KEY (`lecturer_id`);

--
-- Indexes for table `subject_count_prices`
--
ALTER TABLE `subject_count_prices`
  ADD PRIMARY KEY (`count`);

--
-- Indexes for table `subject_ol`
--
ALTER TABLE `subject_ol`
  ADD PRIMARY KEY (`lecturer_id`);

--
-- Indexes for table `subject_table_one`
--
ALTER TABLE `subject_table_one`
  ADD PRIMARY KEY (`admission_no`);

--
-- Indexes for table `subject_table_three`
--
ALTER TABLE `subject_table_three`
  ADD PRIMARY KEY (`admission_no`);

--
-- Indexes for table `subject_table_two`
--
ALTER TABLE `subject_table_two`
  ADD PRIMARY KEY (`admission_no`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`username`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
